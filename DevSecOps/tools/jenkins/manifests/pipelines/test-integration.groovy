// tools/jenkins/manifests/pipelines/test-integration.groovy
pipeline {
    agent any
    
    stages {
        stage('Test Vault') {
            steps {
                withVault(
                    configuration: [
                        vaultCredentialId: 'vault-token',
                        vaultUrl: 'http://vault.security-tools.svc.cluster.local:8200'
                    ],
                    vaultSecrets: [[
                        path: 'secret/data/jenkins/sonarqube',
                        secretValues: [[envVar: 'TEST_SECRET', vaultKey: 'token']]
                    ]]
                ) {
                    sh '''
                        echo "TEST_SECRET exists: $([ ! -z "$TEST_SECRET" ] && echo 'yes' || echo 'no')"
                        echo "Secret value length: ${#TEST_SECRET}"
                    '''
                }
            }
        }
    }
}