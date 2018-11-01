This code implements a Google Cloud AppEngine application that
integrates a Cloud SQL (Postgresql) database with an Endpoints REST
API. It is deployed to a Google Cloud project that has resources for
AppEngine and Cloud SQL. 

The domain of application is the Monopoly database and only the read 
players API endpoint is implemented. 

### Deployment Instructions

See the code for detailed descriptions of the functionality; to install
and configure the system, do the following.

1. Create a new Google App Engine Project according to these instructions:
[Quickstart for Java 8 for App Engine Standard Environment](https://cloud.google.com/appengine/docs/standard/java/quickstart).
Note the following for each section of the tutorial:
    - *Before you begin* &mdash; You only need step 1 here.
    Do step 2 only to configure your own machine. Set the billing on your
    new project to the account number we provide.
    - *Downloading the Hello World app* &mdash; Use the Lab 9 application
    you&rsquo;ve already cloned from the CS 262 code repo.
    - *Running Hello World on your local machine* &mdash; You&rsquo;ll need to
    modify the URI appropriately for this deployment to work. Access only the
    hello-world endpoint; you haven&rsquo;t created an SQL service yet.
    - *Deploying and running Hello World on App Engine* &mdash; Modify the URI
    again. Remember to disable and/or delete your application when it is no longer
    needed to ensure that you don&rsquo;t use up your Google credits.

2. Create a new CloudSQL instance for your new application according to
these instructions:
[Using Cloud SQL for PostgreSQL](https://cloud.google.com/appengine/docs/standard/java/cloud-sql/using-cloud-sql-postgres)
(and Google&rsquo;s [sample code repo](https://github.com/GoogleCloudPlatform/java-docs-samples/tree/master/appengine-java8/cloudsql-postgres)).
Note the following for each section of the tutorial:
    - *Before you begin* &mdash; You&rsquo;ve already done this.
    - *Setting up the Cloud SQL instance* &mdash; *Do as written.*
    - *Granting access to App Engine* &mdash; Skip this step; your CloudSQL is
    built into the same application.
    - *Testing in your development environment* &mdash; There is a 
    [bug](https://stackoverflow.com/questions/50705839/cloudsql-eclipse-java-standard-gae-java-lang-unsatisfiedlinkerror)
    in the Cloud SDK socket factory that prevents it from working locally.
    The work around is to do code development in IDEA and Google Cloud
    project builds and deploys using the Google Cloud SDK shell. Note that 
    each re-deploy creates a backup version of the system that should be deleted
    to save money.
    - *Deploying your app* &mdash; *Do as written.*

3. Create a new CloudEndpoints API for your new application according to
these instructions:
[Getting Started with Endpoints Frameworks on App Engine](https://cloud.google.com/endpoints/docs/frameworks/java/get-started-frameworks-java)
(and Google&rsquo;s [sample code repo](https://github.com/GoogleCloudPlatform/java-docs-samples/tree/master/appengine-java8/endpoints-v2-backend)).
Note the following for each section of the tutorial:
    - *Before you begin* &mdash; You&rsquo;ve already done this.
    - *Installing and configuring required software* &mdash; You&rsquo;ve already done this.
    - *Getting the sample code* &mdash; You&rsquo;ve already done this.
    - *Configuring Endpoints* &mdash; *Do as written.*
    - *Deploying the Endpoints configuration* &mdash; *Do as written.* 
    - *Running the sample locally* &mdash; This may not run because of the bug noted above.
    - *Deploying the API backend* &mdash; *Do as written.*
    - *Sending a request to the API* &mdash; *Do as written.* 
    - *Tracking API activity* &mdash; This is optional.
    - *Clean up* &mdash; Only do this after the lab/homework are graded.

The Google Cloud application/database incur charges when active, and we have limited grant money
to fund our individual assignment and team project development. To cut costs, manually shut the 
system down when not in use by:

- Disabling the AppEngine instance (Dashboard-AppEngine-Settings-Disable)
- Stopping the SQL instance (Dashboard-SQL-Stop)

The system can be restarted by reversing these two actions. Note that it
takes a few minutes for the system to become fully active again.
Note that even a disabled project costs ~$1/day. Delete the project
entirely when it has be graded (of course, keep the source code in your repo).