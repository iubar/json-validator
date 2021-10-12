pipeline {
    agent {    
    	docker {   	
    		image 'iubar-maven-alpine'
    		label 'docker'
    		args '-v ${HOME}/.m2:/home/jenkins/.m2:rw,z -v ${HOME}/.sonar:/home/jenkins/.sonar:rw,z'
    	} 
    }
	options {
		ansiColor('xterm')
	}    
	environment {	
		MAVEN_CLI_OPTS = '--batch-mode --show-version'
	}    
    stages {
        stage ('Build') {
            steps {
                sh 'mvn $MAVEN_CLI_OPTS clean compile'
            }
        }
		stage('Test') {
            steps {
                sh 'mvn $MAVEN_CLI_OPTS -Djava.io.tmpdir=${WORKSPACE}@tmp -Djava.awt.headless=true test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml' // show junit log in Jenkins 
                }
            }
        }
        stage('Analyze') {
            steps {
				sh '''
					echo "SKIP_SONARQUBE: ${SKIP_SONARQUBE}"
					if [ $SKIP_SONARQUBE = true ]; then												
						echo "Skipping sonar-scanner analysis"
            		else
               			sonar-scanner
                	fi
				'''						
            }
        }		
        stage('Quality gate') {	
            steps {
				sh 'wget http://192.168.0.119:8081/artifactory/ext-release-local/jenkins/jenkins-sonar-quality-gate-check.sh --no-check-certificate'
				sh 'chmod +x ./jenkins-sonar-quality-gate-check.sh'
				sh './jenkins-sonar-quality-gate-check.sh true # true / false = Ignore or not the quality gate score
            }
        }
		stage ('Deploy') {
            steps {
                sh 'mvn $MAVEN_CLI_OPTS -DskipTests=true deploy'
            }
        }		
    }
	post {
        success {
        	sh 'mvn $MAVEN_CLI_OPTS dependency:analyze'
			sh 'mvn $MAVEN_CLI_OPTS versions:display-plugin-updates'
			sh 'mvn $MAVEN_CLI_OPTS versions:display-dependency-updates'          
        }	
        changed {
        	echo "CURRENT STATUS: ${currentBuild.currentResult}"
            sh "curl -H 'JENKINS: Pipeline Hook Iubar' -i -X GET -G ${env.IUBAR_WEBHOOK_URL} -d status=${currentBuild.currentResult} -d project_name='${JOB_NAME}'"
        }       
		cleanup {
			cleanWs()
			dir("${env.WORKSPACE}@tmp") {				
				deleteDir()
			}
        }
    }    
}
