pipeline {
    agent { label 'jenkins-agent' }

    environment {
        SONAR_PROJECT_KEY = "test-project"
        VAULT_ADDR = env.VAULT_ADDR // Access the environment variable
    }

    stages {
        stage('Test Vault Integration') {
            steps {
                withVault(
                    configuration: [
                        timeout: 60
                    ],
                    vaultSecrets: [
                        [
                            path: 'secret/data/jenkins/sonarqube',
                            secretValues: [
                                [envVar: 'VAULT_SONAR_TOKEN', vaultKey: 'token']
                            ]
                        ]
                    ]
                ) {
                    sh '''
                        echo "Vault Integration Test"
                        echo "Testing Vault Connection..."
                        if [ ! -z "$VAULT_SONAR_TOKEN" ]; then
                            echo "Successfully retrieved secret from Vault: Secret exists"
                            echo "Vault Sonar Token: $VAULT_SONAR_TOKEN" // Print for verification
                            // You can compare $VAULT_SONAR_TOKEN with a value if you retrieve it from a different source.
                        else
                            echo "Failed to retrieve secret from Vault"
                            exit 1
                        fi
                    '''
                }
            }
        }

        stage('Test SonarQube Connection') {
            steps {
                withSonarQubeEnv('SonarQube') { // Uses SONAR_TOKEN from environment
                    sh '''
                        echo "SonarQube Integration Test"
                        echo "SonarQube URL: ${env.SONAR_HOST_URL}" // Access environment variable
                        curl -u "${env.SONAR_TOKEN}:" "${env.SONAR_HOST_URL}/api/system/status"
                    '''
                }
            }
        }

        stage('Print Environment') {
            steps {
                sh '''
                    echo "Environment Information:"
                    echo "SONAR_PROJECT_KEY: ${SONAR_PROJECT_KEY}"
                    echo "Workspace: ${WORKSPACE}"
                    echo "Node Name: ${NODE_NAME}"
                    echo "Vault Address: ${env.VAULT_ADDR}"
                '''
            }
        }
    }

    post {
        always {
            deleteDir()
        }
    }
}