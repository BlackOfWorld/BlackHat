package me.bow.treecapitatorultimate.Utils;

public class Tuple<A, B> {
    private A a;
    private B b;

    public Tuple(A var0, B var1) {
        this.a = var0;
        this.b = var1;
    }

    public A a() {
        return this.a;
    }

    public B b() {
        return this.b;
    }
}