# jsonUtils
json2Object object2Json
How to

To get a Git project into your build:

gradle

Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
Step 2. Add the dependency

	dependencies {
	        compile 'com.github.hujlin:jsonUtils:v1.0.1'
	}
  
  

maven

Step 1. Add the JitPack repository to your build file
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>Copy
Step 2. Add the dependency

	<dependency>
	    <groupId>com.github.hujlin</groupId>
	    <artifactId>jsonUtils</artifactId>
	    <version>v1.0.1</version>
	</dependency>
  
  //1 json --->User(Object)
  
  User user =  JsonUtils.json2Object(json, User.class);
  
  //2 Object ---->json
  
    User user = new User("name","123");
    String json =  JsonUtils.obj2json(user);

