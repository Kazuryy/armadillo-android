package dev.kazuryy.armadillo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Text
import dev.kazuryy.armadillo.ui.theme.BrandOrange
import dev.kazuryy.armadillo.ui.theme.SecondaryText

@Composable
fun SelfHostedEntryScreen(
    url: String,
    onUrlChange: (String) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    var isFieldFocused by remember { mutableStateOf(false) }
    val fieldFocusRequester = remember { FocusRequester() }
    val continueFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        fieldFocusRequester.requestFocus()
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        BasicTextField(
            value = url,
            onValueChange = onUrlChange,
            singleLine = true,
            textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
            cursorBrush = SolidColor(BrandOrange),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
                continueFocusRequester.requestFocus()
            }),
            modifier = Modifier
                .width(500.dp)
                .focusRequester(fieldFocusRequester)
                .focusProperties { down = continueFocusRequester }
                .onFocusChanged { state ->
                    isFieldFocused = state.isFocused
                    if (state.isFocused) keyboardController?.show()
                }
                .background(Color.White.copy(alpha = 0.06f), RoundedCornerShape(12.dp))
                .border(
                    width = if (isFieldFocused) 2.dp else 0.dp,
                    color = BrandOrange,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) { innerTextField ->
            if (url.isEmpty()) {
                Text(text = "Server URL", color = SecondaryText, fontSize = 18.sp)
            }
            innerTextField()
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.colors(
                    containerColor = Color.Transparent,
                    contentColor = SecondaryText
                )
            ) {
                Text("Back")
            }
            Button(
                onClick = onContinue,
                enabled = url.trim().isNotEmpty(),
                modifier = Modifier.focusRequester(continueFocusRequester),
                colors = ButtonDefaults.colors(
                    containerColor = BrandOrange,
                    contentColor = Color.Black
                )
            ) {
                Text("Continue")
            }
        }
    }
}
