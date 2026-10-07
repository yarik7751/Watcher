package com.yarik.watcher.feature.testgetuserdata

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yarik.watcher.feature.testgetuserdata.data.UserRepository
import com.yarik.watcher.feature.testgetuserdata.datasource.BestUser

class TestGetUserActivity : ComponentActivity() {

    var data by mutableStateOf(BestUser.EMPTY)

    val userRepositoryResult = object : UserRepository.Result {
        override fun onReceive(user: BestUser) {
            data = user
        }
    }

    private var userRepository: UserRepository? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userRepository = UserRepository()
        userRepository?.loadBestUserOfDay()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Button(
                            modifier = Modifier
                                .padding(top = 56.dp)
                                .align(Alignment.CenterHorizontally),
                            onClick = {
                                userRepository?.reloadData()
                            }
                        ) {
                            Text(text = stringResource(R.string.test_user_data_upload))
                        }
                        if (!data.isEmpty()) {
                            Text(
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .padding(horizontal = 16.dp)
                                    .align(Alignment.CenterHorizontally),
                                text = data.toString(),
                            )
                        }
                    }
                }
            }

            DisposableEffect(Unit) {
                userRepository?.subscribe(userRepositoryResult)

                onDispose {
                    userRepository?.unsubscribe(userRepositoryResult)
                }
            }
        }
    }
}