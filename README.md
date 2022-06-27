# PermissionManagement

# Step 1. Add the JitPack repository to your build file
allprojects {\n
		repositories {\n
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
  
# Step 2. Add the dependency

dependencies {
	        implementation 'com.github.pruthviraj99:PermissionManagement:Tag'
	}
