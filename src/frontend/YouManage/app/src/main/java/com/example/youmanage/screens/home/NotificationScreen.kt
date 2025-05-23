package com.example.youmanage.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.youmanage.R
import com.example.youmanage.data.remote.notification.Notification
import com.example.youmanage.data.remote.notification.Object
import com.example.youmanage.screens.project_management.TopBar
import com.example.youmanage.utils.formatToRelativeTime
import com.example.youmanage.viewmodel.auth.AuthenticationViewModel
import com.example.youmanage.viewmodel.home.NotificationViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationScreen(
    paddingValues: PaddingValues = PaddingValues(),
    onItemClick: (Object) -> Unit,
    onNavigateBack: () -> Unit,
    haveLeading: Boolean,
    notificationViewModel: NotificationViewModel = hiltViewModel(),
    authenticationViewModel: AuthenticationViewModel = hiltViewModel()
) {

    val accessToken = authenticationViewModel.accessToken.collectAsState(null)
    val notifications by notificationViewModel.notifications.observeAsState()
    val isLoading by notificationViewModel.isLoading.collectAsState()

    LaunchedEffect(accessToken.value) {
        accessToken.value?.let {
            supervisorScope {
               val job = launch {
                    notificationViewModel.getNotifications(
                        authorization = "Bearer $it"
                    )
                }

                job.join()
            }

        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(MaterialTheme.colorScheme.background),

        topBar = {
            TopBar(
                title = "Notifications",
                color = Color.Transparent,
                haveLeading = haveLeading,
                trailing = {
                    IconButton(
                        onClick = {

                                    notificationViewModel.readAll("Bearer ${accessToken.value}")

                                  },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.read_all_icon),
                            contentDescription = "Read all",
                            tint = Color(0xff4CAF50),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                onNavigateBack = onNavigateBack
            )
        }
    ) { it ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Show "No Activity Log" when no logs are available
            if (notifications == null || notifications?.isEmpty() == true) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "No Notification",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Display each activity
                    itemsIndexed(notifications ?: emptyList()) { index, item ->
                        NotificationItem(
                            item,
                            onMarkAsRead = {
                                if(item.id != null){
                                    notificationViewModel.markAsRead(
                                        notificationId = item.id,
                                        authorization = "Bearer ${accessToken.value}"
                                    )
                                }
                            },
                            onDelete = {
                                if(item.id != null){
                                    notificationViewModel.deleteNotification(
                                        notificationId = item.id,
                                        authorization = "Bearer ${accessToken.value}"
                                    )
                                }
                            },

                            onClick = {
                                notificationViewModel.markAsRead(
                                    notificationId = item.id ?: -1,
                                    authorization = "Bearer ${accessToken.value}"
                                )
                                onItemClick(it)
                            }
                        )

                        val size = notifications?.size ?: 0
                        // Trigger loading when scrolled to the last item
                        if (index == size - 1 && !isLoading) {
                            notificationViewModel.getMoreActivityLogs(
                                authorization = "Bearer ${accessToken.value}"
                            )
                        }
                    }
                }

                // Show loading spinner when fetching more logs
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(bottom = 100.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationItem(
    notification: Notification = Notification(),
    onClick: (com.example.youmanage.data.remote.notification.Object) -> Unit,
    onMarkAsRead: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(20.dp)) // Corner radius tăng thêm cho mềm mại
            .clickable {
                onClick(notification.objectContent ?: Object())
            },
        shape = RoundedCornerShape(20.dp), // Các góc được bo tròn mềm mại hơn
        border = BorderStroke(
            1.dp,
            if (notification.isRead == true) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary // Màu sắc nhẹ nhàng hơn
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background // Màu nền dễ chịu
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp), // Padding bao quanh lớn hơn để tạo không gian thoải mái
            horizontalAlignment = Alignment.Start // Căn trái để dễ nhìn hơn
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp), // Padding giữa các phần tử ít hơn để không quá dày
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notification",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)

                )

                Text(
                    text = notification.title ?: "No Title", // Tên mặc định nếu không có title
                    style = TextStyle(
                        fontWeight = FontWeight.Medium, // Font nhẹ nhàng hơn
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier.padding(start = 12.dp)
                        .weight(1f)
                )

                IconWithDropdownMenu(
                    onMarkAsRead = onMarkAsRead,
                    onDelete = onDelete
                )
            }

            Text(
                text = notification.body ?: "No content available",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Normal
                ),
                modifier = Modifier.padding(start = 12.dp, top = 6.dp)
            )

            Spacer(modifier = Modifier.height(8.dp)) // Khoảng cách nhẹ giữa các thành phần

            Text(
                text = if (notification.createdAt != null) formatToRelativeTime(notification.createdAt) else "Just now",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(start = 12.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun IconWithDropdownMenu(
    onMarkAsRead: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) } // Trạng thái menu

    Box(
        modifier = modifier
    ) {
        // Icon
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "More Options",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(24.dp)
                .clickable { expanded = true } // Mở menu khi nhấn vào icon
        )

        // DropdownMenu
        DropdownMenu(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            expanded = expanded,
            tonalElevation = 10.dp,
            onDismissRequest = { expanded = false } // Đóng menu khi nhấn bên ngoài
        ) {
            // Mark as Read Option
            DropdownMenuItem(
                text = { Text("Mark as Read") },
                colors = MenuDefaults.itemColors(textColor = MaterialTheme.colorScheme.primary),
                onClick = {
                    expanded = false // Đóng menu
                    onMarkAsRead() // Gọi hàm xử lý
                }
            )

            // Delete Option
            DropdownMenuItem(
                text = { Text("Delete") },
                colors = MenuDefaults.itemColors(textColor = MaterialTheme.colorScheme.primary),
                onClick = {
                    expanded = false // Đóng menu
                    onDelete() // Gọi hàm xử lý
                }
            )
        }
    }
}