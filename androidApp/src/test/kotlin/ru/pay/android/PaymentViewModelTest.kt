package ru.pay.android
import androidx.lifecycle.SavedStateHandle
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*
import ru.pay.domain.*
import ru.pay.android.presentation.PaymentViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentViewModelTest {
    private val dispatcher=StandardTestDispatcher()
    private lateinit var repo: PaymentRepository
    @Before fun setup() {Dispatchers.setMain(dispatcher);repo=mockk(relaxed=true);every {repo.hasSession()} returns false}
    @After fun close() {Dispatchers.resetMain()}
    private fun vm(saved: SavedStateHandle=SavedStateHandle())=PaymentViewModel(repo,SendPayment(repo),SearchRecipients(repo),saved)
    @Test fun `query restored with saved state`() {
        val saved=SavedStateHandle();vm(saved).changeQuery("мобильная")
        assertEquals("мобильная",vm(saved).query.value)
    }
    @Test fun `clear query removes stale results`()=runTest {
        val vm=vm();coEvery {repo.search(any())} returns listOf(SearchItem("a","A","","SERVICE","a"))
        vm.changeQuery("A");vm.submitSearch();advanceUntilIdle()
        assertEquals(1,vm.search.value.results.size)
        vm.changeQuery("");assertTrue(vm.search.value.results.isEmpty());assertFalse(vm.search.value.submitted)
    }
    @Test fun `retry executes failed query`()=runTest {
        val vm=vm();coEvery {repo.search("mobile")} throws java.io.IOException()
        vm.changeQuery("mobile");vm.submitSearch();advanceUntilIdle()
        assertNotNull(vm.search.value.error)
        coEvery {repo.search("mobile")} returns emptyList()
        vm.submitSearch(true);advanceUntilIdle()
        coVerify(exactly=2) {repo.search("mobile")};assertNull(vm.search.value.error)
    }
    @Test fun `loading visible while request in flight`()=runTest {
        val gate=CompletableDeferred<List<SearchItem>>()
        coEvery {repo.search(any())} coAnswers {gate.await()}
        val vm=vm();vm.changeQuery("mobile");vm.submitSearch();runCurrent()
        assertTrue(vm.search.value.loading)
        gate.complete(emptyList());advanceUntilIdle();assertFalse(vm.search.value.loading)
    }
    @Test fun `old search cannot replace newer results`()=runTest {
        coEvery {repo.search("old")} coAnswers {delay(1000);listOf(SearchItem("old","old","","SERVICE","old"))}
        coEvery {repo.search("new")} returns listOf(SearchItem("new","new","","SERVICE","new"))
        val vm=vm();vm.changeQuery("old");vm.submitSearch();runCurrent()
        vm.changeQuery("new");vm.submitSearch();advanceUntilIdle()
        assertEquals("new",vm.search.value.results.single().id)
    }
    @Test fun `selecting result records server history`()=runTest {
        val item=SearchItem("a","A","","SERVICE","a")
        coEvery {repo.select("a")} returns listOf(item)
        val vm=vm();vm.select(item);advanceUntilIdle()
        assertEquals(listOf(item),vm.search.value.history);assertEquals("Перевод",vm.tab.value)
    }
}
