# lib-colladamodel

A minecraft library mod that adds the ability to load models from COLLADA files.

## For Users

Download latest version: [lib-colladamodel v1.0a1](https://github.com/hea3ven/lib-colladamodel/releases/download/1.0a1/lib-colladamodel-1.0a1.jar)

## For Developers

### Configuration

Add the following lines to your build.gradle script:

```
repositories {
    maven {
        url = "https://raw.github.com/hea3ven/lib-colladamodel/mvn-repo/"
    }
}

dependencies {
    compile 'com.hea3ven:lib-colladamodel-deobf:1.0a1'
}
```

also add the library as a dependency of your mod:

```
@Mod(modid = "yourmodid", version = "1.0", dependencies = "required-after:colladamodel@[1.0a1,)")
public class YourMod {
    // ...
}
```

and run "gradle setupDecompWorkspace".

### Usage

To load a model use the following code:

```
IModelAnimationCustom model = (IModelAnimationCustom)AdvancedModelLoader.loadModel("yourmodid:models/yourmodel.dae")
```

