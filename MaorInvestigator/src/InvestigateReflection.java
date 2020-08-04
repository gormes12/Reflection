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
        Class<?>[] argsClasses = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            argsClasses[i] = args[i].getClass();
        }

        try {
            Method func = instanceOfSomething.getClass().getDeclaredMethod(methodName, argsClasses);
            result = (int) func.invoke(instanceOfSomething, args);
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            return -1;
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
                } catch (InstantiationException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                    return null;
                }
            }
        }

        return newInstance;
    }

    @Override
    public Object elevateMethodAndInvoke(String name, Class<?>[] parametersTypes, Object... args) {
        Object result = null;
        try {
            Method methodToInvoke = instanceOfSomething.getClass().getDeclaredMethod(name, parametersTypes);
            if (!Modifier.isPublic(methodToInvoke.getModifiers())) {
                methodToInvoke.setAccessible(true);
            }
            result = methodToInvoke.invoke(instanceOfSomething, args);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
        return result;
    }

    @Override
    public String getInheritanceChain(String delimiter) {
        return inheritanceChain(instanceOfSomething.getClass(), delimiter);
    }

    public String inheritanceChain(Class<?> obj, String delimiter){
        if(obj.getSuperclass().getSimpleName().equals("Object")){
            return "Object".concat(delimiter).concat(obj.getSimpleName());
        }

        String result = inheritanceChain(obj.getSuperclass(), delimiter);
        return result.concat(delimiter).concat(obj.getSimpleName());
    }
}
