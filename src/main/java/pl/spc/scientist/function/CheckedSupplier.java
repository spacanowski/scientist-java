package pl.spc.scientist.function;

@FunctionalInterface
public interface CheckedSupplier<T> {
    T get() throws Exception;
}
