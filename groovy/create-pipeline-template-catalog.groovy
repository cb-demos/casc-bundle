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
  
} else {
  logger.info("$name job already exists")
}