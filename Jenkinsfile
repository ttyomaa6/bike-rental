pipeline {
    agent any

    parameters {
        booleanParam(
            name: 'RUN_CONDITIONAL',
            defaultValue: false,
            description: 'Выполнить условный этап Conditional'
        )
    }

    stages {
        stage('Build') {
            steps {
                sh 'mvn compile'
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
