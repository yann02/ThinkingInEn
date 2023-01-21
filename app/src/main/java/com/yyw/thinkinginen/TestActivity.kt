package com.yyw.thinkinginen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Surface
import androidx.compose.material.Text
import com.yyw.thinkinginen.ui.theme.ThinkingInEnTheme

class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ThinkingInEnTheme {
                Surface {
                    Column {
                        Text(text = "Hello")
                        Surface {
                            Column {
                                Text(text = "George")
                                Surface {
                                    Text(text = "Can you help me?")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}