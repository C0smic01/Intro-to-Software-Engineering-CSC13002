from django.urls import path
from .views import (
    ProjectListCreateView,
    ProjectRetrieveUpdateDestroyView,
    ProjectMemberManagementView,
    TaskListCreateView,
    TaskRetrieveUpdateDestroyView,
    CommentListCreateView,
    CommentRetrieveUpdateDestroyView,
    RoleListCreateView,
    RoleRetrieveUpdateDestroyView,
    RoleManagementView,
)

urlpatterns = [
    path('projects/', ProjectListCreateView.as_view(), name='project-list-create'),
    path('projects/<int:pk>/', ProjectRetrieveUpdateDestroyView.as_view(), name='project-detail'),
    path('projects/<int:pk>/members/<str:action>/', ProjectMemberManagementView.as_view(), name='project-member-management'),
    path('projects/<int:project_id>/tasks/', TaskListCreateView.as_view(), name='task-list-create'),
    path('projects/<int:project_id>/tasks/<int:pk>/', TaskRetrieveUpdateDestroyView.as_view(), name='task-detail'),
    path('projects/<int:project_id>/tasks/<int:task_id>/comments/', CommentListCreateView.as_view(), name='comment-list-create'),
    path('projects/<int:project_id>/tasks/<int:task_id>/comments/<int:pk>/', CommentRetrieveUpdateDestroyView.as_view(), name='comment-detail'),
    path('projects/<int:pk>/roles/', RoleListCreateView.as_view(), name='role-list-create'),
    path('projects/<int:project_id>/roles/<int:pk>/', RoleRetrieveUpdateDestroyView.as_view(), name='role-retrieve-update-destroy'),
    path('projects/<int:project_id>/roles/<int:pk>/<str:action>/', RoleManagementView.as_view(), name='role-management'),
]
