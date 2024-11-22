package com.example.youmanage

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.example.youmanage.data.remote.authentication.ChangePasswordRequest
import com.example.youmanage.data.remote.authentication.VerifyRequest
import com.example.youmanage.navigation.RootNavGraph
import com.example.youmanage.screens.project_management.ProjectDetailScreen
import com.example.youmanage.screens.task_management.CreateTaskScreen
import com.example.youmanage.screens.task_management.TaskListScreen
import com.example.youmanage.ui.theme.YouManageTheme
import com.example.youmanage.viewmodel.AuthenticationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        //val viewModel: AuthenticationViewModel by viewModels()
//
//        val project = ProjectCreate(
//            "My Project Update 1",
//            "2024-04-09",
//            Host(email = "string@gmail.com", username= "string"),
//            "derat"
//        )
//
//        val authorization = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoxNzI5MjU5MzQ5LCJpYXQiOjE3MjkwODY1NDksImp0aSI6IjdiMmU1NzVkNjc1NDQ5NjViOWNmYTY2MGU3NWEzMzA1IiwidXNlcl9pZCI6MX0.wITO6OwnhH5smemrfuj6aPeBwrbcRFQc_QOZoF9pgcQ"
//
//        runBlocking {
//            viewModel.deleteProject(
//                id = "3",
//                authorization = authorization
//            )
//        }


//        viewModel.verifyResetPasswordOTP(
//            VerifyRequest(
//                "duonghuutuong0712@gmail.com",
//                "320760"
//            )
//        )

//        viewModel.changePassword(
//            ChangePasswordRequest(
//                "string",
//                "string",
//                "40782c00-09ce-4e07-8315-fa8e3891a572"
//            )
//        )

        setContent {
            YouManageTheme {

              //  CreateTaskScreen()
              //  TaskListScreen()
            //ProjectDetailScreen()
                //FindUserScreen()
               RootNavGraph()
                //TestPieChart()


                //viewModel.sendOTP(SendOTPRequest("duonghuutuong0712@gmail.com"))

            }
        }
    }
}





