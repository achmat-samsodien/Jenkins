import hudson.model.StringParameterDefinition

node {
   // Mark the code checkout 'stage'....
   stage 'Checkout'

   // Get some code from a GitHub repository
   git url: 'http://git.interfront.local/scm/rla/icbs-rla-parent.git'

    // Java SDK
    jdk = "/opt/java7"
    env.JAVA_HOME = "${jdk}"
    
    // Get the maven tool.
    def mvnHome = '/opt/maven3'

    // Mark the code build 'stage'....r
    stage 'Build'
    // Run the maven build
    sh "${mvnHome}/bin/mvn clean install"

   //define variables (could be done in a better way)
   stage 'Extract Dataloaders'
   def ws = "/home/jenkins/workspace/rla-production-deploy"
   def ci = "/home/jenkins/workspace/rla-production-deploy/ci-scripts/scripts/bash/jenkins"
   def data="/home/jenkins/workspace/rla-production-deploy/target/app_pack/zip"
   def app="/home/jenkins/workspace/rla-production-deploy/target/app_pack/war"
   def ear="/home/jenkins/workspace/rla-production-deploy/target/app_pack/ear"
   def deploy="/home/jenkins/workspace/rla-production-deploy/ci-scripts/scripts/servers-properties"
   def ENTITY = "${ENTITY}"
   
   echo "Extracting CI Scripts"
   
   zipFile = "${data}/ci-scripts-*.zip"
   
   sh "unzip -o ${zipFile} -d ${ws}"
   
   echo "Extracting Dataloader"
   
   zipFile = "${data}/icbs-bpm-data-loader-*.zip"
   sh "unzip ${zipFile} -d ${data}"
   
   zipFile = "${data}/icbs-cm-central-data-loader-*.zip"
   sh "unzip ${zipFile} -d ${data}"
   
   zipFile = "${data}/icbs-rla-branch-data-loader-*.zip"
   sh "unzip ${zipFile} -d ${data}"
   
   zipFile = "${data}/icbs-rla-data-loader-*.zip"
   sh "unzip ${zipFile} -d ${data}"
   
   zipFile = "${data}/icbs-uam-central-data-loader-*.zip"
   sh "unzip ${zipFile} -d ${data}"
   
   zipFile = "${data}/library-data-loader-*.zip"
   sh "unzip ${zipFile} -d ${data}"
   
   zipFile = "${data}/trdm-data-loader-*.zip"
   sh "unzip ${zipFile} -d ${data}"
   
   echo "Removing Archives"
   sh 'rm -f /home/jenkins/workspace/rla-production-deploy/target/app_pack/zip/*.zip'
   
   stage 'Creating App Directories'
   
   sh "mkdir ${app}/central"

   sh "mv ${app}/bpm* ${app}/central"

   sh "mv ${app}/*-central* ${app}/central"

   sh "mv ${ear}/icbs-trdm-* ${app}/central"

   sh "mv ${app}/library* ${app}/central"

   sh "mv ${app}/uam-service-* ${app}/central"

   sh "mkdir ${app}/branch"

   sh "mv ${app}/*.war ${app}/branch"
   
   stage "Deploying Central Applications"
   
   sh "${ci}/deploy-pipeline-all.sh $ENTITY"
   
   stage 'Deploying ICBS BPM Dataloader'
   

   def BPM = "icbs-bpm-data-loader"
   def BPM_SCRIPTS_DIR = "/home/jenkins/workspace/rla-production-deploy/target/app_pack/zip/$BPM/dba_scripts"
  
   sh "${ci}/dataload-parent.sh $ENTITY $BPM_SCRIPTS_DIR"
   
   stage 'Deploying ICBS RLA Dataloader'
   
   def RLA = "icbs-rla-data-loader"
   def RLA_SCRIPTS_DIR = "/home/jenkins/workspace/rla-production-deploy/target/app_pack/zip/$RLA/dba_scripts"
  
   sh "${ci}/dataload-parent.sh $ENTITY $RLA_SCRIPTS_DIR"
   
   stage 'Deploying ICBS CM Central Dataloader'
   
   def CM = "icbs-cm-central-data-loader"
   def CM_SCRIPTS_DIR = "/home/jenkins/workspace/rla-production-deploy/target/app_pack/zip/$CM/dba_scripts"
  
   sh "${ci}/dataload-parent.sh $ENTITY $CM_SCRIPTS_DIR"
   
   stage 'Deploying Library Dataloader'
   
   def LIB = "library-data-loader"
   def LIB_SCRIPTS_DIR = "/home/jenkins/workspace/rla-production-deploy/target/app_pack/zip/$LIB/dba_scripts"
  
   sh "${ci}/dataload-parent.sh $ENTITY $LIB_SCRIPTS_DIR"
   
   stage 'Deploying TRDM Dataloader'
   
   def TRDM = "trdm-data-loader"
   def TRDM_SCRIPTS_DIR = "/home/jenkins/workspace/rla-production-deploy/target/app_pack/zip/$TRDM/dba_scripts"
  
   sh "${ci}/dataload-parent.sh $ENTITY $TRDM_SCRIPTS_DIR"
   
   stage 'Deploying ICBS UAM Dataloader'
   
   def UAM = "icbs-uam-central-data-loader"
   def UAM_SCRIPTS_DIR = "/home/jenkins/workspace/rla-production-deploy/target/app_pack/zip/$UAM/dba_scripts"
  
   sh "${ci}/dataload-parent.sh $ENTITY $UAM_SCRIPTS_DIR"
   
   stage 'Deploying ICBS RLA Branch Dataloader'
   
   def RLAB = "icbs-rla-branch-data-loader"
   def RLAB_SCRIPTS_DIR = "/home/jenkins/workspace/rla-production-deploy/target/app_pack/zip/$UAM/dba_scripts"
  
   sh "${ci}/dataload-parent.sh $ENTITY $RLAB_SCRIPTS_DIR"
   
  
}
