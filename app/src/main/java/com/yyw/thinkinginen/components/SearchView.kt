package com.yyw.thinkinginen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
//import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yyw.thinkinginen.MainViewModel
import com.yyw.thinkinginen.R
import com.yyw.thinkinginen.entities.vo.ViewMessage
import com.yyw.thinkinginen.ui.theme.ThinkingInEnTheme
import kotlinx.coroutines.delay


@Composable
fun SearchView(model: MainViewModel = viewModel(), onBack: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), tonalElevation = 5.dp) {
        Column {
            val viewMessages by model.mMatchViewMessages.collectAsState()
            val (text, textChange) = remember {
                mutableStateOf(TextFieldValue(""))
            }
            TopViewForSearch(text = text, textChange = textChange, onBack = onBack)
            Divider(color = MaterialTheme.colorScheme.outline)
            LaunchedEffect(text) {
                delay(100)
                model.updatedSearchText(text = text.text)
            }
            MatchedSentences(data = viewMessages)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TopViewForSearch(text: TextFieldValue, textChange: (TextFieldValue) -> Unit, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusRequester = remember { FocusRequester() }
        TextField(
            value = text,
            onValueChange = textChange,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            leadingIcon = {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                }
            },
            trailingIcon = {
                if (text.text.isNotEmpty()) {
                    IconButton(onClick = {
                        textChange(TextFieldValue(""))
                        keyboardController?.show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            placeholder = {
                Text(
                    text = stringResource(id = R.string.search_hint),
                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                keyboardController?.hide()
            }),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface)
        )
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}

@Composable
fun MatchedSentences(data: List<ViewMessage>) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(data, key = {
            it.messageId
        }) {
            ItemForMatchedSentences(msg = it)
        }
    }
}

@Composable
fun ItemForMatchedSentences(msg: ViewMessage) {
    Box(
        modifier = Modifier
            .padding(
                start = dimensionResource(id = R.dimen.padding_left_item), end = dimensionResource(
                    id = R.dimen.padding_right_item
                )
            )
            .height(dimensionResource(id = R.dimen.item_height_one_line)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = msg.content, style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
@Preview
fun PreSearchView() {
    ThinkingInEnTheme {
        TopViewForSearch(text = TextFieldValue(), textChange = {}, onBack = {})
    }
}

@Composable
@Preview
fun PreMatchedSentences() {
    ThinkingInEnTheme {
        MatchedSentences(preViewMessages)
    }
}

val preViewMessages = listOf(
    ViewMessage(1, 1, 1, "Muddy Puddles", "Peppa", "I love muddy puddles", "我最喜欢在泥坑里玩了"),
    ViewMessage(2, 1, 1, "Muddy Puddles", "Peppa", "I love muddy puddles", "我最喜欢在泥坑里玩了"),
    ViewMessage(3, 1, 1, "Muddy Puddles", "Peppa", "I love muddy puddles", "我最喜欢在泥坑里玩了")
)