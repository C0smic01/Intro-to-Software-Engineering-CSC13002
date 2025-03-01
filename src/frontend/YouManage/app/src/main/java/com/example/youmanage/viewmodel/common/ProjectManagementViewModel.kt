package com.example.youmanage.viewmodel.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.youmanage.data.remote.authentication.Message
import com.example.youmanage.data.remote.projectmanagement.GanttChartData
import com.example.youmanage.data.remote.projectmanagement.Id
import com.example.youmanage.data.remote.projectmanagement.Progress
import com.example.youmanage.data.remote.projectmanagement.Project
import com.example.youmanage.data.remote.projectmanagement.ProjectCreate
import com.example.youmanage.data.remote.projectmanagement.Projects
import com.example.youmanage.data.remote.projectmanagement.User
import com.example.youmanage.data.remote.projectmanagement.UserId
import com.example.youmanage.data.remote.taskmanagement.Detail
import com.example.youmanage.data.remote.taskmanagement.Username
import com.example.youmanage.data.remote.websocket.MemberObject
import com.example.youmanage.data.remote.websocket.WebSocketResponse
import com.example.youmanage.repository.ProjectManagementRepository
import com.example.youmanage.repository.WebSocketRepository
import com.example.youmanage.utils.Resource
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProjectManagementViewModel @Inject constructor(
    private val repository: ProjectManagementRepository,
    private val webSocketRepository: WebSocketRepository
) : ViewModel() {

    private val supervisorJob = SupervisorJob()
    private val viewModelScopeWithSupervisor = CoroutineScope(Dispatchers.Main + supervisorJob)

    private val _projects = MutableLiveData<Resource<Projects>>()
    val projects: LiveData<Resource<Projects>> get() = _projects

    private val _projectSearched = MutableLiveData<Resource<Projects>>()
    val projectSearched: LiveData<Resource<Projects>> get() = _projectSearched

    private val _project = MutableLiveData<Resource<Project>>()
    val project: LiveData<Resource<Project>> get() = _project

    private val _deleteProjectResponse = MutableLiveData<Resource<String>>()
    val deleteProjectResponse: LiveData<Resource<String>> get() = _deleteProjectResponse

    private val _progress = MutableLiveData<Resource<Progress>>()
    val progress: LiveData<Resource<Progress>> get() = _progress

    private val _addMemberResponse = MutableLiveData<Resource<Detail>>()
    val addMemberResponse: LiveData<Resource<Detail>> get() = _addMemberResponse

    private val _deleteMemberResponse = MutableLiveData<Resource<Detail>>()
    val deleteMemberResponse: LiveData<Resource<Detail>> get() = _deleteMemberResponse

    private val _members = MutableLiveData<Resource<List<User>>>()
    val members: LiveData<Resource<List<User>>> get() = _members

    private val _ganttChartData = MutableLiveData<Resource<List<GanttChartData>>>()
    val ganttChartData: LiveData<Resource<List<GanttChartData>>> get() = _ganttChartData

    private val _projectSocket = MutableLiveData<Resource<WebSocketResponse<Project>>>()
    val projectSocket: LiveData<Resource<WebSocketResponse<Project>>> get() = _projectSocket

    private val _memberSocket = MutableLiveData<Resource<WebSocketResponse<MemberObject>>>()
    val memberSocket: LiveData<Resource<WebSocketResponse<MemberObject>>> get() = _memberSocket

    private val _updateProjectResponse = MutableLiveData<Resource<Project>>()
    val updateProjectResponse: LiveData<Resource<Project>> get() = _updateProjectResponse

    private val _quitResponse = MutableLiveData<Resource<Detail>>()
    val quitResponse: LiveData<Resource<Detail>> get() = _quitResponse

    private val _empowerResponse = MutableLiveData<Resource<Message>>()
    val empowerResponse: LiveData<Resource<Message>> get() = _empowerResponse

    private val _createProjectResponse = MutableLiveData<Resource<Project>>()
    val createProjectResponse: LiveData<Resource<Project>> get() = _createProjectResponse

    private val _isHost = MutableLiveData<Boolean>(false)
    val isHost: LiveData<Boolean> get() = _isHost

    fun isHost(
        id: String,
        authorization: String
    ){
        viewModelScopeWithSupervisor.launch {
            val response = withContext(Dispatchers.IO){
                repository.isHost(id, authorization)
            }
            if(response is Resource.Success){
                _isHost.value = response.data?.isHost ?: false
            } else{
                _isHost.value = false
            }
        }
    }

    fun getProjectList(authorization: String) {
        viewModelScopeWithSupervisor.launch {
            val response = withContext(Dispatchers.IO){
                repository.getProjectList(authorization = authorization)
            }
            _projects.value = response
        }
    }

    fun searchProject(q: String, authorization: String) {
        viewModelScopeWithSupervisor.launch {
            val response = withContext(Dispatchers.IO){
                repository.searchProject(q, authorization)
            }
            _projects.value = repository.searchProject(q, authorization)
        }
    }

    fun createProject(project: ProjectCreate, authorization: String) {
        viewModelScopeWithSupervisor.launch {
            _createProjectResponse.value = repository.createProject(
                project = project,
                authorization = authorization)
        }
    }

    fun getProject(id: String, authorization: String) {
        viewModelScopeWithSupervisor.launch {
            _project.value = repository.getProject(id = id, authorization = authorization)
        }
    }

    fun updateFullProject(id: String, project: ProjectCreate, authorization: String) {
        viewModelScopeWithSupervisor.launch {
            _updateProjectResponse.value = repository.updateFullProject(id = id, project = project, authorization = authorization)
        }
    }

    fun updateProject(id: String, project: ProjectCreate, authorization: String) {
        viewModelScopeWithSupervisor.launch {
            _updateProjectResponse.value = repository.updateProject(id = id, project = project, authorization = authorization)
        }
    }

    fun deleteProject(id: String, authorization: String) {
        viewModelScopeWithSupervisor.launch {
            _deleteProjectResponse.value = repository.deleteProject(
                id = id,
                authorization = authorization
            )
        }
    }

    fun addMember(id: String, username: Username, authorization: String) {
        viewModelScopeWithSupervisor.launch {
            _addMemberResponse.value = repository.addMember(
                id = id,
                member = username,
                authorization = authorization
            )
        }
    }

    fun removeMember(id: String, memberId: Id, authorization: String) {
        viewModelScopeWithSupervisor.launch {
            _deleteMemberResponse.value = repository.removeMember(
                id = id,
                memberId = memberId,
                authorization = authorization
            )
        }
    }

    fun getMembers(id: String, authorization: String) {
        viewModelScopeWithSupervisor.launch {
            _members.value = repository.getMembers(
                id = id,
                authorization = authorization
            )
        }
    }

    fun getProgressTrack(id: String, authorization: String) {
        viewModelScopeWithSupervisor.launch {
            _progress.value = repository.getProgressTrack(
                projectId = id,
                authorization = authorization
            )
        }
    }

    fun connectToProjectWebsocket(url: String) {
        viewModelScopeWithSupervisor.launch {
            webSocketRepository.connectToSocket(
                url,
                object : TypeToken<WebSocketResponse<Project>>() {},
                _projectSocket
            )
        }
    }

    fun connectToMemberWebsocket(url: String) {
        viewModelScopeWithSupervisor.launch {
            webSocketRepository.connectToSocket(
                url,
                object : TypeToken<WebSocketResponse<MemberObject>>() {},
                _memberSocket
            )
        }
    }

    fun getGanttChartData(id: String, authorization: String) {
        viewModelScopeWithSupervisor.launch {
            _ganttChartData.value = repository.getGanttChartData(
                id = id,
                authorization = authorization
            )
        }
    }

    fun quitProject(
        id: String,
        authorization: String
    ){
        viewModelScopeWithSupervisor.launch {
            _quitResponse.value = repository.quitProject(
                id = id,
                authorization = authorization
            )
        }
    }

    fun empower(
        id: String,
        userId: UserId,
        authorization: String
    ){
        viewModelScopeWithSupervisor.launch {
            _empowerResponse.value = repository.empower(
                id = id,
                userId,
                authorization = authorization
            )
        }
    }

}
