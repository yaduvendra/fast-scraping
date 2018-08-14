package com.fastscraping.util;

public interface DataValue<T> {
    T get();
    boolean equals(DataValue compareWith);

    class NullValue<T> implements DataValue{
        @Override
        public T get() {
            return null;
        }

        @Override
        public boolean equals(DataValue compareWith) {
            return this.get() == null && this.get() == compareWith.get();
        }

        @Override
        public String toString() {
            return "";
        }
    }

    class NonNullValue<T> implements DataValue {
        private final T value;

        public NonNullValue(T value) throws NullValueException {
            if(value != null) {
                this.value = value;
            } else {
                throw new NullValueException("The NonNullValue can't be initialized with null.");
            }
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public String toString(){
            return value.toString();
        }

        @Override
        public boolean equals(DataValue compareWith) {
            return this.get() == compareWith.get();
        }
    }
}

