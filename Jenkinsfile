pipeline {
    agent {
        node {
            label 'CustomNode'
        }
    }
    environment {
        PYTHON_VERSION = '3.12'
        TOOLS_DIR = "${env.WORKSPACE}/tools"
    }
    options {
        checkoutToSubdirectory('src')
        ansiColor('xterm')
    }
    stages {
        stage('Install Python') {
            steps {
                dir('src') {
                    sh 'python'
                }
            }
        }
    }
}