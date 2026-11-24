pipeline {
    agent any

    options {
        // Checkout выполняется явным этапом ниже
        skipDefaultCheckout()
    }

    parameters {
        booleanParam(
            name: 'RUN_CONDITIONAL',
            defaultValue: false,
            description: 'Выполнить условный этап Conditional'
        )
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                sh 'git log -1 --oneline'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn compile'
            }
        }

        stage('Static Analysis') {
            steps {
                sh 'mvn checkstyle:check'
            }
            post {
                always {
                    archiveArtifacts artifacts: 'target/checkstyle-result.xml', allowEmptyArchive: true
                }
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Conditional') {
            when {
                expression { return params.RUN_CONDITIONAL }
            }
            steps {
                echo 'Условный этап выполняется: RUN_CONDITIONAL = true'
                sh 'mvn -B help:evaluate -Dexpression=project.artifactId -q -DforceStdout'
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }
    }
}
