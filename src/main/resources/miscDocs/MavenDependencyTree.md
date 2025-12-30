mvn dependency:tree | grep org.json     
    to print dependency tree that has org.json
mvn dependency:tree -Dincludes=com.vaadin.external.google:android-json -Dscope=test
    to print dependency tree that has com.vaadin.external.google:android-json in one of test packages
