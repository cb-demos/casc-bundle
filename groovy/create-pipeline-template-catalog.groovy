import jenkins.model.*;
import org.jenkinsci.plugins.workflow.libs.*;
import jenkins.scm.api.SCMSource;
import jenkins.plugins.git.*; 
import com.cloudbees.pipeline.governance.templates.*;
import com.cloudbees.pipeline.governance.templates.catalog.*;
import org.jenkinsci.plugins.github.GitHubPlugin;
import java.util.logging.Logger;

Logger logger = Logger.getLogger("create-pipeline-template-catalog.groovy");

def jenkins = Jenkins.instance
def name = "Insurance Frontend"
def insuranceFrontendJob = jenkins.getItemByFullName(name)
if (insuranceFrontendJob == null) {
  //Pipeline Template Catalog
  SCMSource scm = new GitSCMSource("https://github.com/cb-demos/pipeline-template-catalog.git");
  scm.setCredentialsId("github-sa");
  TemplateCatalog catalog = new TemplateCatalog(scm, "master");
  catalog.setUpdateInterval("1h");
  GlobalTemplateCatalogManagement.get().addCatalog(catalog);
  GlobalTemplateCatalogManagement.get().save();
  logger.info("Creating new Pipeline Template Catalog");
  catalog.updateFromSCM(); 

  //microblog-fronted job from Pipeline Template
  def frontendJobXml = """
  <org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject plugin="workflow-multibranch@2.26">
  <properties>
    <com.cloudbees.pipeline.governance.templates.classic.multibranch.GovernanceMultibranchPipelinePropertyImpl plugin="cloudbees-workflow-template@3.12">
      <instance>
        <model>cb-demo/react-app</model>
        <values class="tree-map">
          <entry>
            <string>gcpProject</string>
            <string>core-flow-research</string>
          </entry>
          <entry>
            <string>githubCredentialId</string>
            <string>github-sa</string>
          </entry>
          <entry>
            <string>name</string>
            <string>Insurance Frontend</string>
          </entry>
          <entry>
            <string>repoOwner</string>
            <string>cb-demos</string>
          </entry>
          <entry>
            <string>repository</string>
            <string>insurance-frontend</string>
          </entry>
        </values>
      </instance>
    </com.cloudbees.pipeline.governance.templates.classic.multibranch.GovernanceMultibranchPipelinePropertyImpl>
  </properties>
  <folderViews class="jenkins.branch.MultiBranchProjectViewHolder" plugin="branch-api@2.6.5">
    <owner class="org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject" reference="../.." />
  </folderViews>
  <healthMetrics />
  <icon class="jenkins.branch.MetadataActionFolderIcon" plugin="branch-api@2.6.5">
    <owner class="org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject" reference="../.." />
  </icon>
  <orphanedItemStrategy class="com.cloudbees.hudson.plugins.folder.computed.DefaultOrphanedItemStrategy" plugin="cloudbees-folder@6.16">
    <pruneDeadBranches>true</pruneDeadBranches>
    <daysToKeep>-1</daysToKeep>
    <numToKeep>-1</numToKeep>
  </orphanedItemStrategy>
  <triggers>
    <com.cloudbees.hudson.plugins.folder.computed.PeriodicFolderTrigger plugin="cloudbees-folder@6.16">
      <spec>H H/4 * * *</spec>
      <interval>86400000</interval>
    </com.cloudbees.hudson.plugins.folder.computed.PeriodicFolderTrigger>
  </triggers>
  <disabled>false</disabled>
  <sources>
    <jenkins.branch.BranchSource plugin="branch-api@2.6.5">
      <source class="org.jenkinsci.plugins.github_branch_source.GitHubSCMSource" plugin="github-branch-source@2.11.2">
        <id>React</id>
        <apiUri>https://api.github.com</apiUri>
        <credentialsId>github-sa</credentialsId>
        <repoOwner>cb-demos</repoOwner>
        <repository>insurance-frontend</repository>
        <traits>
          <org.jenkinsci.plugins.github__branch__source.BranchDiscoveryTrait>
            <strategyId>1</strategyId>
          </org.jenkinsci.plugins.github__branch__source.BranchDiscoveryTrait>
          <org.jenkinsci.plugins.github__branch__source.OriginPullRequestDiscoveryTrait>
            <strategyId>1</strategyId>
          </org.jenkinsci.plugins.github__branch__source.OriginPullRequestDiscoveryTrait>
          <org.jenkinsci.plugins.github__branch__source.ForkPullRequestDiscoveryTrait>
            <strategyId>1</strategyId>
            <trust class="org.jenkinsci.plugins.github_branch_source.ForkPullRequestDiscoveryTrait$TrustPermission" />
          </org.jenkinsci.plugins.github__branch__source.ForkPullRequestDiscoveryTrait>
        </traits>
      </source>
    </jenkins.branch.BranchSource>
  </sources>
  <factory class="com.cloudbees.pipeline.governance.templates.classic.multibranch.FromTemplateBranchProjectFactory" plugin="cloudbees-workflow-template@3.12">
    <owner class="org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject" reference="../.." />
    <catalogName>cb-demo</catalogName>
    <templateDirectory>react-app</templateDirectory>
  </factory>
</org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject>
  """

  def p = jenkins.createProjectFromXML(name, new ByteArrayInputStream(frontendJobXml.getBytes("UTF-8")));

  logger.info("created $name job")
  def backendName = "Insurance Backend"
  //microblog-backend job from Pipeline Template
  def backendJobXml = """
  <org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject plugin="workflow-multibranch@2.26">
  <properties>
    <com.cloudbees.pipeline.governance.templates.classic.multibranch.GovernanceMultibranchPipelinePropertyImpl plugin="cloudbees-workflow-template@3.12">
      <instance>
        <model>cb-demo/python-poetry</model>
        <values class="tree-map">
          <entry>
            <string>gcpProject</string>
            <string>core-flow-research</string>
          </entry>
          <entry>
            <string>githubCredentialId</string>
            <string>github-sa</string>
          </entry>
          <entry>
            <string>name</string>
            <string>Insurance Backend</string>
          </entry>
          <entry>
            <string>repoOwner</string>
            <string>cb-demos</string>
          </entry>
          <entry>
            <string>repository</string>
            <string>insurance-backend</string>
          </entry>
        </values>
      </instance>
    </com.cloudbees.pipeline.governance.templates.classic.multibranch.GovernanceMultibranchPipelinePropertyImpl>
  </properties>
  <icon class="jenkins.branch.MetadataActionFolderIcon" plugin="branch-api@2.6.5">
    <owner class="org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject" reference="../.." />
  </icon>
  <orphanedItemStrategy class="com.cloudbees.hudson.plugins.folder.computed.DefaultOrphanedItemStrategy" plugin="cloudbees-folder@6.16">
    <pruneDeadBranches>true</pruneDeadBranches>
  </orphanedItemStrategy>
  <triggers>
    <com.cloudbees.hudson.plugins.folder.computed.PeriodicFolderTrigger plugin="cloudbees-folder@6.16">
      <spec>H H/4 * * *</spec>
      <interval>86400000</interval>
    </com.cloudbees.hudson.plugins.folder.computed.PeriodicFolderTrigger>
  </triggers>
  <disabled>false</disabled>
  <sources>
    <jenkins.branch.BranchSource plugin="branch-api@2.6.5">
      <source class="org.jenkinsci.plugins.github_branch_source.GitHubSCMSource" plugin="github-branch-source@2.11.2">
        <id>Python</id>
        <apiUri>https://api.github.com</apiUri>
        <credentialsId>github-sa</credentialsId>
        <repoOwner>cb-demos</repoOwner>
        <repository>insurance-backend</repository>
        <traits>
          <org.jenkinsci.plugins.github__branch__source.BranchDiscoveryTrait>
            <strategyId>1</strategyId>
          </org.jenkinsci.plugins.github__branch__source.BranchDiscoveryTrait>
          <org.jenkinsci.plugins.github__branch__source.OriginPullRequestDiscoveryTrait>
            <strategyId>1</strategyId>
          </org.jenkinsci.plugins.github__branch__source.OriginPullRequestDiscoveryTrait>
          <org.jenkinsci.plugins.github__branch__source.ForkPullRequestDiscoveryTrait>
            <strategyId>1</strategyId>
            <trust class="org.jenkinsci.plugins.github_branch_source.ForkPullRequestDiscoveryTrait$TrustPermission" />
          </org.jenkinsci.plugins.github__branch__source.ForkPullRequestDiscoveryTrait>
        </traits>
      </source>
    </jenkins.branch.BranchSource>
  </sources>
  <factory class="com.cloudbees.pipeline.governance.templates.classic.multibranch.FromTemplateBranchProjectFactory" plugin="cloudbees-workflow-template@3.12">
    <owner class="org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject" reference="../.." />
    <catalogName>cb-demo</catalogName>
    <templateDirectory>python-poetry</templateDirectory>
  </factory>
</org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject>
  """

  def backendProject = jenkins.createProjectFromXML(backendName, new ByteArrayInputStream(backendJobXml.getBytes("UTF-8")));

  logger.info("created $backendName job")  
  
} else {
  logger.info("$name job already exists")
}