/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.interview.cls;

import com.interview.cls.sealed.shape.Circle;
import com.interview.cls.sealed.shape.ColoredRectangle;
import com.interview.cls.sealed.shape.FilledRectangle;
import com.interview.cls.sealed.shape.Rectangle;
import com.interview.cls.sealed.shape.Shape;
import com.interview.cls.sealed.shape.Square;
import com.interview.cls.sealed.shape.TransparentRectangle;
import com.interview.cls.sealed.shape.Triangle;

/**
 *
 * @author javau
 */
public class SealedClassMain {

    public static void main(String[] args) {
        Shape circle = new Circle(5.0);
        Shape rectangle = new Rectangle(4.0, 6.0);

        System.out.println("Circle area: " + calculateArea(circle));
        System.out.println("Rectangle area: " + calculateArea(rectangle));
    }

    public static double calculateArea(Shape shape) {
        return switch (shape) {
            case Circle c ->
                c.area();
            case TransparentRectangle r ->
                r.area();
            case ColoredRectangle r ->
                r.area();
            case FilledRectangle r ->
                r.area();
            case Rectangle r ->
                r.area();
            case Square r ->
                r.area();
            case Triangle t ->
                t.area();
            // No default needed - exhaustive because Shape is sealed
        };
    }

    public static double calculateArea2(Shape shape) {
        return switch (shape) {
            case Circle t ->
                t.area();
            case Rectangle t ->
                t.area();
            case Triangle t ->
                t.area();
            // No 'default' case is required here because Shape is sealed and all permitted subclasses are handled.
            // If Triangle was a direct permitted subclass of Shape, it would need its own case.
            // Since Triangle extends Square (which is non-sealed), it falls under the 'Square' case.
            default ->
                0;
        };
    }

}
/*
To write a sealed class in Java, follow these steps:
    Declare the class as sealed: Add the sealed modifier to the class declaration.
    Specify permitted subclasses: Use the permits clause after the extends or implements clauses to explicitly list the classes that
        are allowed to directly extend the sealed class.
    Define permitted subclasses: Each permitted subclass must be declared with one of the following modifiers:
        final: This prevents further extension of the subclass.
        sealed: This means the subclass itself is also sealed and requires its own permits clause.
        non-sealed: This allows the subclass to be extended by any class.

Key Points:
    Sealed classes and their permitted subclasses must reside in the same module or, if in an unnamed module, in the same package.
    The permits clause can be omitted if the permitted subclasses are defined in the same source file as the sealed class, as the
        compiler can infer them.
    Sealed classes provide a way to create a controlled and closed type hierarchy, limiting which classes can extend a particular
        class or implement an interface.
 */
