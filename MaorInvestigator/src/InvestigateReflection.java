import reflection.api.*;
import java.lang.reflect.*;
import java.util.HashSet;
import java.util.Set;

public class InvestigateReflection implements Investigator {

    private Object instanceOfSomething;

    @Override
    public void load(Object anInstanceOfSomething) {
        instanceOfSomething = anInstanceOfSomething;
    }

    @Override
    public int getTotalNumberOfMethods() {
        Method[] methods = instanceOfSomething.getClass().getDeclaredMethods();
        return methods.length;
    }

    @Override
    public int getTotalNumberOfConstructors() {
        Constructor[] constructors = instanceOfSomething.getClass().getDeclaredConstructors();
        return constructors.length;
    }

    @Override
    public int getTotalNumberOfFields() {
        Field[] fields = instanceOfSomething.getClass().getDeclaredFields();
        return fields.length;
    }

    @Override
    public Set<String> getAllImplementedInterfaces() {
        Class[] interfaceClasses = instanceOfSomething.getClass().getInterfaces();
        Set<String> implementInterfaceNames = new HashSet<>(interfaceClasses.length);

        for (Class interFace : interfaceClasses) {
            implementInterfaceNames.add(interFace.getSimpleName());
        }

        return implementInterfaceNames;
    }

    @Override
    public int getCountOfConstantFields() {
        int countOfConstantFields = 0;
        Field[] fields = instanceOfSomething.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (Modifier.isFinal(field.getModifiers())) {
                countOfConstantFields++;
            }
        }

        return countOfConstantFields;
    }

    @Override
    public int getCountOfStaticMethods() {
        int countOfStaticMethods = 0;
        Method[] methods = instanceOfSomething.getClass().getDeclaredMethods();

        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers())) {
                countOfStaticMethods++;
            }
        }

        return countOfStaticMethods;
    }

    @Override
    public boolean isExtending() {
        return !(instanceOfSomething.getClass().getSuperclass().getSimpleName().equals("Object"));
    }

    @Override
    public String getParentClassSimpleName() {
        return instanceOfSomething.getClass().getSuperclass().getSimpleName();
    }

    @Override
    public boolean isParentClassAbstract() {
        return (Modifier.isAbstract(instanceOfSomething.getClass().getSuperclass().getModifiers()));
    }

    @Override
    public Set<String> getNamesOfAllFieldsIncludingInheritanceChain() {
        Field[] fields;
        Set<String> namesOfAllFields = new HashSet<>();
        Class currClass = instanceOfSomething.getClass();

        while (!currClass.getSimpleName().equals("Object")) {
            fields = currClass.getDeclaredFields();
            for (Field field : fields) {
                namesOfAllFields.add(field.getName());
            }
            currClass = currClass.getSuperclass();
        }

        return namesOfAllFields;
    }

    @Override
    public int invokeMethodThatReturnsInt(String methodName, Object... args) {
        int result = 0;
        Class[] argsClasses = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            argsClasses[i] = args[i].getClass();
        }

        try {
            Method func = instanceOfSomething.getClass().getDeclaredMethod(methodName, argsClasses);
            if (args.length == 0) {
                result = (int) func.invoke(instanceOfSomething);
            } else {
                result = (int) func.invoke(instanceOfSomething, args);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public Object createInstance(int numberOfArgs, Object... args) {
        Object newInstance = null;
        Constructor[] constructors = instanceOfSomething.getClass().getDeclaredConstructors();
        for (Constructor ctor : constructors) {
            if (ctor.getParameterCount() == numberOfArgs) {
                try {
                    newInstance = ctor.newInstance(args);
                    break;
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        return newInstance;
    }

    @Override
    public Object elevateMethodAndInvoke(String name, Class<?>[] parametersTypes, Object... args) {
        Method[] methods = instanceOfSomething.getClass().getDeclaredMethods();
        Object result = null;
        boolean isEqual;
        Class<?>[] methodParametersTypes;
        for (Method method : methods) {
            if (method.getName().equals(name)) {
                isEqual = true;
                methodParametersTypes = method.getParameterTypes();
                for (int i = 0; i < methodParametersTypes.length; i++) {
                    if (!parametersTypes[i].getName().equals(methodParametersTypes[i].getName())) {
                        isEqual = false;
                        break;
                    }
                }
                if (isEqual) {
                    try {
                        if(!Modifier.isPublic(method.getModifiers())){
                            method.setAccessible(true);
                        }
                        result = method.invoke(instanceOfSomething, args);
                        break;
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }

    @Override
    public String getInheritanceChain(String delimiter) {
        return inheritanceChain(instanceOfSomething.getClass(), delimiter);
    }

    public String inheritanceChain(Class obj, String delimiter){
        if(obj.getSuperclass().getSimpleName().equals("Object")){
            return "Object".concat(delimiter).concat(obj.getSimpleName());
        }

        String result = inheritanceChain(obj.getSuperclass(), delimiter);
        return result.concat(delimiter).concat(obj.getSimpleName());
    }
}
