from rest_framework import serializers
from django.contrib.auth import get_user_model
from django.db import models
from .models import Project, Task, Comment, Role, Issue, ChangeRequest
User = get_user_model()

class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ['id', 'username', 'email']
        ref_name = 'ProjectManagerUser'
        

class ProjectSerializer(serializers.ModelSerializer):
    host = UserSerializer(read_only=True)
    members = UserSerializer(many=True, read_only=True)

    class Meta:
        model = Project
        fields = ['id', 'name', 'description', 'duedate', 'host', 'members', 'created_at', 'updated_at']
        read_only_fields = ['host', 'created_at', 'updated_at']
        
    def validate_duedate(self, value):
        """
        Check that the duedate is not before the latest end_date of tasks in the project.
        """
        project = self.instance
        if project:
            latest_task_end_date = project.tasks.aggregate(models.Max('end_date'))['end_date__max']
            if latest_task_end_date and value < latest_task_end_date:
                raise serializers.ValidationError("The project duedate cannot be before the latest end_date of tasks in the project.")
        return value
    
class TaskIssueSerializer(serializers.ModelSerializer):
    class Meta:
        model = Task
        fields = ['id', 'title', 'description', 'status']
        
        
class IssueSerializer(serializers.ModelSerializer):
    reporter = UserSerializer(read_only=True)
    assignee = UserSerializer(read_only=True)
    assignee_id = serializers.PrimaryKeyRelatedField(queryset=User.objects.all(), source='assignee', write_only=True, required=False)
    task = TaskIssueSerializer(read_only=True)
    task_id = serializers.PrimaryKeyRelatedField(queryset=Task.objects.all(), source='task', write_only=True, required=False)

    class Meta:
        model = Issue
        fields = [
            'id', 
            'title', 
            'description', 
            'status', 
            'project', 
            'reporter', 
            'assignee', 
            'assignee_id',
            'task',
            'task_id',
        ]
        read_only_fields = ['reporter', 'project']

    def validate_assignee_id(self, value):
        project = self.context['project']
        if value not in project.members.all():
            raise serializers.ValidationError("Assignee must be a member of the project.")
        return value

        
class TaskSerializer(serializers.ModelSerializer):
    assignee = UserSerializer(read_only=True)
    assignee_id = serializers.PrimaryKeyRelatedField(queryset=User.objects.all(), source='assignee', write_only=True, required=False)
    comments_count = serializers.SerializerMethodField()

    class Meta:
        model = Task
        fields = [
            'id', 
            'title', 
            'description', 
            'start_date', 
            'end_date', 
            'status',
            'priority',
            'project', 
            'assignee',
            'assignee_id',
            'comments_count',
            'created_at',
            'updated_at',
            ]
        read_only_fields = ['project', 'created_at', 'updated_at']
        
    def validate(self, data):
        """Validate date relationships"""
        # Check planned dates
        if data.get('start_date') and data.get('end_date'):
            if data['start_date'] > data['end_date']:
                raise serializers.ValidationError({
                    "end_date": "End date must be after start date"
                })

        # Validate against project dates if creating new task
        project = self.context.get('project')
        if project:
            if data.get('start_date') and data['start_date'] > project.duedate:
                raise serializers.ValidationError({
                    "start_date": "Task start date cannot be after project due date"
                })
            if data.get('end_date') and data['end_date'] > project.duedate:
                raise serializers.ValidationError({
                    "end_date": "Task end date cannot be after project due date"
                })

        return data

    def validate_assignee_id(self, value):
        project = self.context['project']
        if value not in project.members.all():
            raise serializers.ValidationError("Assignee must be a member of the project.")
        return value
    
    def get_comments_count(self, obj):
        return obj.comments.count()
    

class CommentSerializer(serializers.ModelSerializer):
    author = UserSerializer(read_only=True)

    class Meta:
        model = Comment
        fields = ['id', 'content', 'author', 'created_at', 'updated_at']
        read_only_fields = ['author', 'created_at', 'updated_at']
        

class CommentDetailSerializer(serializers.ModelSerializer):
    author = UserSerializer(read_only=True)

    class Meta:
        model = Comment
        fields = ['id', 'content', 'author', 'created_at', 'updated_at', 'task']
        read_only_fields = ['author', 'created_at', 'updated_at', 'task']

        
class RoleSerializer(serializers.ModelSerializer):
    class Meta:
        model = Role
        fields = ['id', 'role_name', 'description', 'project']
        read_only_fields = ['project']
    

class ProjectMemberSerializer(serializers.ModelSerializer):
    members = UserSerializer(many=True, read_only=True)
    
    class Meta:
        model = Project
        fields = ['members']
        
        
class ChangeRequestSerializer(serializers.ModelSerializer):
    class Meta:
        model = ChangeRequest
        fields = ['id', 'project', 'requester', 'request_type', 'target_table', 'target_table_id', 'description', 'new_data', 'created_at', 'reviewed_by', 'reviewed_at', 'declined_reason', 'status', 'system_description']
        read_only_fields = ['project', 'requester', 'status', 'created_at', 'reviewed_by', 'reviewed_at', 'system_description']
        

class TaskGanttChartSerializer(serializers.ModelSerializer):
    class Meta:
        model = Task
        fields = ['id', 'title', 'start_date', 'end_date']