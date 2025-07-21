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
        stage('Get info') {
            steps {
                dir('src') {
                    script {
                        env.CI_COMMIT_SHORT_SHA = sh(script: 'git rev-parse HEAD', returnStdout: true).trim().take(8)
                        env.CI_PROJECT_NAME = sh(script: '''git remote show origin -n | grep Fetch | cut -d "/" -f5 | cut -d "." -f1''', returnStdout: true).trim()
                        env.CI_ORGANIZATION_NAME = sh(script: "git remote show origin -n | grep Fetch | cut -d '/' -f4", returnStdout: true).trim()
                        env.DOCKER_IMAGE_NAME = "gitea.onelab.internal/${CI_ORGANIZATION_NAME}/${CI_PROJECT_NAME}"
                        env.CONTAINER_NAME = "${CI_PROJECT_NAME}-${CI_COMMIT_SHORT_SHA}"
                    }
                }
            }
        }

        stage('Verify Python') {
            steps {
                dir('src') {
                    sh 'python --version'
                    sh 'env | sort'
                }
            }
        }

        stage('Print Build Environment') {
            steps {
                sh '''
                    echo "Printing environment variables:"
                    env | sort
                '''
            }
        }
    }
}