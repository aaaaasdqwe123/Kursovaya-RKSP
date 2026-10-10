package ru.pay.android.presentation
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.pay.domain.*

@Composable fun PaymentRoot(vm: PaymentViewModel=hiltViewModel()) {
    val ui by vm.ui.collectAsStateWithLifecycle()
    val tab by vm.tab.collectAsStateWithLifecycle()
    MaterialTheme(colorScheme=if(ui.dark) darkColorScheme(primary=Color(0xFF8FBCFF)) else lightColorScheme(primary=Color(0xFF185ABD))) {
        Scaffold { padding ->
            Column(Modifier.fillMaxSize().padding(padding).imePadding()) {
                if(ui.busy) LinearProgressIndicator(Modifier.fillMaxWidth())
                ui.error?.let { Notice(it,true,vm::dismiss) }
                ui.message?.let { Notice(it,false,vm::dismiss) }
                if(!ui.authenticated) AuthScreen(ui.busy,vm::auth)
                else {
                    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal=8.dp),verticalAlignment=Alignment.CenterVertically) {
                        listOf("Главная","Перевод","Поиск","Карты","История","Настройки").forEach { name ->
                            FilterChip(selected=tab==name,onClick={ vm.navigate(name) },label={ Text(name) },modifier=Modifier.padding(3.dp))
                        }
                    }
                    when(tab) {
                        "Главная" -> Dashboard(ui,vm)
                        "Перевод" -> TransferScreen(ui,vm)
                        "Поиск" -> SearchScreen(vm)
                        "Карты" -> CardsScreen(ui,vm)
                        "История" -> TransactionsScreen(ui,vm)
                        else -> SettingsScreen(ui,vm)
                    }
                }
            }
        }
    }
}
@Composable private fun Notice(text: String,error: Boolean,onDismiss: ()->Unit) {
    Surface(color=if(error) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer) {
        Row(Modifier.fillMaxWidth().padding(8.dp),verticalAlignment=Alignment.CenterVertically) {
            Text(text,modifier=Modifier.weight(1f)); TextButton(onClick=onDismiss) { Text("Закрыть") }
        }
    }
}
@Composable private fun Title(text: String) { Text(text,style=MaterialTheme.typography.headlineSmall,modifier=Modifier.padding(vertical=12.dp)) }
@Composable private fun AuthScreen(busy: Boolean,submit: (Boolean,String,String,String,String)->Unit) {
    var register by rememberSaveable { mutableStateOf(false) }
    var email by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") } // never store passwords in saved instance state
    var name by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
        Text("ПЛАТЕЖИ",style=MaterialTheme.typography.labelLarge,color=MaterialTheme.colorScheme.primary)
        Title(if(register) "Создать аккаунт" else "Добро пожаловать")
        Text("Учебная электронная платёжная система")
        OutlinedTextField(email,{email=it},label={Text("Email")},singleLine=true,keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Email),modifier=Modifier.fillMaxWidth())
        OutlinedTextField(password,{password=it},label={Text("Пароль")},supportingText={if(register) Text("От 12 до 128 символов")},visualTransformation=PasswordVisualTransformation(),singleLine=true,keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Password),modifier=Modifier.fillMaxWidth())
        if(register) {
            OutlinedTextField(name,{name=it},label={Text("Имя")},singleLine=true,modifier=Modifier.fillMaxWidth())
            OutlinedTextField(phone,{phone=it},label={Text("Телефон")},placeholder={Text("+79991234567")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Phone),singleLine=true,modifier=Modifier.fillMaxWidth())
        }
        Button(onClick={ submit(register,email,password,name,phone) },enabled=!busy,modifier=Modifier.fillMaxWidth()) { Text(if(register) "Зарегистрироваться" else "Войти") }
        TextButton(onClick={register=!register},enabled=!busy) { Text(if(register) "Уже есть аккаунт? Войти" else "Создать аккаунт") }
        Text("Используются демонстрационные средства",style=MaterialTheme.typography.bodySmall)
    }
}
@Composable private fun Dashboard(ui: UiState,vm: PaymentViewModel) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
        Title("Здравствуйте, ${ui.profile?.name ?: "…"}")
        Card(Modifier.fillMaxWidth(),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.primaryContainer)) {
            Column(Modifier.padding(24.dp)) {
                Text("Баланс кошелька · RUB")
                Text(ui.wallet?.let { Money.format(it.balance) } ?: "Загрузка…",style=MaterialTheme.typography.headlineLarge)
                Text("Демонстрационные средства",style=MaterialTheme.typography.labelSmall)
            }
        }
        Button(onClick={vm.navigate("Перевод")},modifier=Modifier.fillMaxWidth()) { Text("Перевести деньги") }
        OutlinedButton(onClick={vm.navigate("Поиск")},modifier=Modifier.fillMaxWidth()) { Text("Найти получателя или услугу") }
        if(ui.pending!=null) TextButton(onClick={vm.navigate("Перевод")}) { Text("Проверить незавершённый перевод") }
        OutlinedButton(onClick=vm::refresh,enabled=!ui.busy) { Text("Обновить баланс") }
        Title("Последние операции")
        if(ui.transactions.isEmpty()) Text("Операций пока нет")
        ui.transactions.take(5).forEach { TransactionRow(it) }
    }
}
@Composable private fun TransferScreen(ui: UiState,vm: PaymentViewModel) {
    val recipient by vm.recipient.collectAsStateWithLifecycle()
    val amount by vm.amount.collectAsStateWithLifecycle()
    val note by vm.note.collectAsStateWithLifecycle()
    val kind by vm.kind.collectAsStateWithLifecycle()
    var confirm by remember { mutableStateOf(false) }
    var validation by remember { mutableStateOf<String?>(null) }
    val editable=ui.pending==null && !ui.busy
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
        Title("Перевод и оплата")
        Row(Modifier.horizontalScroll(rememberScrollState())) {
            listOf("PHONE" to "Телефон","CARD" to "Карта","SERVICE" to "Услуга").forEach { (value,label)->
                FilterChip(kind==value,{vm.field("kind",value)},label={Text(label)},enabled=editable,modifier=Modifier.padding(end=8.dp))
            }
        }
        Text(if(kind=="CARD") "Введите ID виртуальной карты получателя" else if(kind=="SERVICE") "Выберите услугу через поиск или введите её код" else "Перевод зарегистрированному пользователю")
        OutlinedTextField(recipient,{vm.field("recipient",it)},label={Text(if(kind=="PHONE") "Телефон получателя" else "Идентификатор получателя")},placeholder={Text(if(kind=="PHONE") "+79990000002" else if(kind=="SERVICE") "mobile" else "UUID карты")},enabled=editable,singleLine=true,modifier=Modifier.fillMaxWidth())
        OutlinedTextField(amount,{vm.field("amount",it)},label={Text("Сумма, ₽")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal),enabled=editable,singleLine=true,modifier=Modifier.fillMaxWidth())
        OutlinedTextField(note,{vm.field("note",it.take(200))},label={Text("Сообщение получателю")},enabled=editable,modifier=Modifier.fillMaxWidth())
        validation?.let { Text(it,color=MaterialTheme.colorScheme.error) }
        if(ui.pending!=null) Text("Результат предыдущего запроса не подтверждён. Повторная проверка использует тот же ключ и не создаёт второе списание.")
        Button(onClick={
            validation=null
            try { Money.parse(amount); require(recipient.isNotBlank()) { "Укажите получателя" }; confirm=true }
            catch(e: IllegalArgumentException) { validation=e.message }
        },enabled=!ui.busy,modifier=Modifier.fillMaxWidth()) { Text(if(ui.pending!=null) "Проверить / повторить" else "Продолжить") }
    }
    if(confirm) AlertDialog(onDismissRequest={confirm=false},title={Text("Подтверждение перевода")},text={Text("Получатель: $recipient\nСумма: $amount ₽\nКомиссия: 0 ₽")},confirmButton={TextButton(onClick={confirm=false; vm.pay()}) { Text("Подтвердить") }},dismissButton={TextButton(onClick={confirm=false}) { Text("Отмена") }})
}
@Composable private fun SearchScreen(vm: PaymentViewModel) {
    val query by vm.query.collectAsStateWithLifecycle()
    val state by vm.search.collectAsStateWithLifecycle()
    val ui by vm.ui.collectAsStateWithLifecycle()
    var focused by remember { mutableStateOf(false) }
    val keyboard=LocalSoftwareKeyboardController.current
    val focus=LocalFocusManager.current
    Column(Modifier.fillMaxSize().padding(horizontal=20.dp)) {
        Title("Поиск")
        OutlinedTextField(query,vm::changeQuery,placeholder={Text("Телефон, email или услуга")},singleLine=true,
            trailingIcon={if(query.isNotEmpty()) TextButton(onClick={vm.changeQuery(""); keyboard?.hide(); focus.clearFocus()}) {Text("Очистить")}},
            keyboardOptions=KeyboardOptions(imeAction=ImeAction.Search),keyboardActions=KeyboardActions(onSearch={keyboard?.hide(); vm.submitSearch()}),
            modifier=Modifier.fillMaxWidth().onFocusChanged { focused=it.isFocused })
        Button(onClick={keyboard?.hide(); vm.submitSearch()},enabled=query.isNotBlank() && !state.loading) { Text("Найти") }
        if(state.loading) LinearProgressIndicator(Modifier.fillMaxWidth())
        if(state.error!=null) {
            Text(state.error!!,color=MaterialTheme.colorScheme.error)
            OutlinedButton(onClick={vm.submitSearch(retry=true)}) { Text("Обновить") }
        }
        if(query.isEmpty() && focused) {
            Row(verticalAlignment=Alignment.CenterVertically) {
                Text("История поиска",modifier=Modifier.weight(1f))
                if(state.history.isNotEmpty()) TextButton(onClick=vm::clearHistory,enabled=!ui.busy) { Text("Очистить историю") }
            }
            if(state.history.isEmpty()) Text("История пока пуста")
            LazyColumn { items(state.history,key={it.id}) { SearchRow(it,!ui.busy) {vm.select(it)} } }
        } else {
            if(!state.loading && state.error==null && state.submitted && state.results.isEmpty()) Text("Ничего не найдено. Попробуйте другой запрос")
            if(!state.submitted) Text("Получателя можно найти по полному телефону или email",style=MaterialTheme.typography.bodySmall)
            LazyColumn { items(state.results,key={it.id}) { SearchRow(it,!ui.busy) {vm.select(it)} } }
        }
    }
}
@Composable private fun SearchRow(item: SearchItem,enabled: Boolean,onClick: ()->Unit) {
    ListItem(headlineContent={Text(item.title)},supportingContent={Text(item.subtitle)},modifier=Modifier.fillMaxWidth().clickable(enabled=enabled,onClick=onClick))
    HorizontalDivider()
}
@Composable private fun CardsScreen(ui: UiState,vm: PaymentViewModel) {
    var alias by rememberSaveable { mutableStateOf("") }
    var deleting by remember { mutableStateOf<Card?>(null) }
    val clipboard=LocalClipboardManager.current
    LazyColumn(Modifier.fillMaxSize().padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
        item { Title("Карты и кошелёк"); Text("Виртуальные карты используют общий баланс кошелька") }
        item { Text(ui.wallet?.let {Money.format(it.balance)} ?: "Загрузка…") }
        items(ui.cards,key={it.id}) { card ->
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(card.alias,style=MaterialTheme.typography.titleMedium); Text("•••• ${card.last4}")
                    Text(card.id,style=MaterialTheme.typography.bodySmall)
                    TextButton(onClick={clipboard.setText(AnnotatedString(card.id))}) {Text("Скопировать ID для перевода")}
                    TextButton(onClick={deleting=card},enabled=!ui.busy) {Text("Удалить карту")}
                }
            }
        }
        item {
            if(ui.cards.isEmpty()) Text("У вас пока нет виртуальных карт")
            OutlinedTextField(alias,{alias=it.take(40)},label={Text("Название новой карты")},singleLine=true,modifier=Modifier.fillMaxWidth())
            Button(onClick={vm.addCard(alias)},enabled=!ui.busy && alias.isNotBlank() && ui.cards.size<5) {Text("Создать виртуальную карту")}
        }
    }
    deleting?.let { card -> AlertDialog(onDismissRequest={deleting=null},title={Text("Удалить карту?")},text={Text("${card.alias}. Баланс кошелька сохранится.")},confirmButton={TextButton(onClick={deleting=null;vm.deleteCard(card.id)}) {Text("Удалить")}},dismissButton={TextButton(onClick={deleting=null}) {Text("Отмена")}}) }
}
@Composable private fun TransactionRow(tx: Transaction) {
    ListItem(headlineContent={Text(tx.counterparty)},supportingContent={Column {Text(tx.note.ifBlank {if(tx.kind=="SERVICE") "Оплата услуги" else "Перевод"}); Text(tx.createdAt)}},trailingContent={Text((if(tx.incoming) "+" else "−")+Money.format(tx.amount),color=if(tx.incoming) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)})
    HorizontalDivider()
}
@Composable private fun TransactionsScreen(ui: UiState,vm: PaymentViewModel) {
    val query by vm.transactionQuery.collectAsStateWithLifecycle()
    val filter by vm.filter.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Title("История операций")
        OutlinedTextField(query,{vm.field("transactionQuery",it.take(100))},placeholder={Text("Имя или сообщение")},singleLine=true,modifier=Modifier.fillMaxWidth())
        Row(Modifier.horizontalScroll(rememberScrollState())) {
            listOf("ALL" to "Все","IN" to "Входящие","OUT" to "Исходящие").forEach { (id,label) ->
                FilterChip(filter==id,{vm.field("filter",id)},label={Text(label)},modifier=Modifier.padding(end=8.dp))
            }
        }
        OutlinedButton(onClick=vm::transactions,enabled=!ui.busy) {Text("Применить / обновить")}
        if(ui.transactions.isEmpty()) Text("Операции не найдены")
        LazyColumn {
            items(ui.transactions,key={it.id}) {TransactionRow(it)}
            if(ui.nextOffset!=null) item {TextButton(onClick=vm::more,enabled=!ui.busy) {Text("Загрузить ещё")}}
        }
    }
}
@Composable private fun SettingsScreen(ui: UiState,vm: PaymentViewModel) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)) {
        Title("Профиль и настройки")
        Text(ui.profile?.name ?: ""); Text(ui.profile?.email ?: ""); Text(ui.profile?.phone ?: "")
        Row(verticalAlignment=Alignment.CenterVertically) {Text("Тёмная тема",modifier=Modifier.weight(1f)); Switch(ui.dark,vm::dark,enabled=!ui.busy)}
        Text("Настройка сохраняется в вашем аккаунте")
        OutlinedButton(onClick=vm::refresh,enabled=!ui.busy) {Text("Обновить")}
        Button(onClick=vm::logout,enabled=!ui.busy) {Text("Выйти")}
    }
}
