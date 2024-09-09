.class public test4
.super java/lang/Object

.field private static _sysin Ljava/util/Scanner;
.field private static b Z
.field private static h Ljava/lang/String;
.field private static n F

;
; Runtime input scanner
;
.method static <clinit>()V

	new	java/util/Scanner
	dup
	getstatic	java/lang/System/in Ljava/io/InputStream;
	invokespecial	java/util/Scanner/<init>(Ljava/io/InputStream;)V
	putstatic	test4/_sysin Ljava/util/Scanner;
	return

.limit locals 16
.limit stack 16
.end method

;
; Main class constructor
;
.method public <init>()V
.var 0 is this Ltest4;

	aload_0
	invokespecial	java/lang/Object/<init>()V
	return

.limit locals 16
.limit stack 16
.end method

;
; MAIN
;
.method public static main([Ljava/lang/String;)V
.var 0 is args [Ljava/lang/String;
.var 1 is _start Ljava/time/Instant;
.var 2 is _end Ljava/time/Instant;
.var 3 is _elapsed J

	invokestatic	java/time/Instant/now()Ljava/time/Instant;
	astore_1

	ldc	5.0
	putstatic	test4/n F
	iconst_1
	putstatic	test4/b Z
	ldc	"Y"
	putstatic	test4/h Ljava/lang/String;
	getstatic	test4/n F
	ldc	5.0
	fcmpg
	ifeq	L003
	iconst_0
	goto	L004
L003:
	iconst_1
L004:
	ifeq	L002
L001:
	getstatic	test4/n F
	ldc	5.0
	fcmpg
	ifeq	L005
	iconst_0
	goto	L006
L005:
	iconst_1
L006:
	ifeq	L002
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"%s\n"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	ldc	"Hello"
	aastore
	invokevirtual	java/io/PrintStream/printf(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;
	pop
	getstatic	test4/n F
	ldc	5.0
	fcmpg
	ifeq	L007
	iconst_0
	goto	L008
L007:
	iconst_1
L008:
	ifeq	L002
	getstatic	test4/h Ljava/lang/String;
	ldc	"Y"
	invokevirtual	java/lang/String.compareTo(Ljava/lang/String;)I
	ifeq	L011
	iconst_0
	goto	L012
L011:
	iconst_1
L012:
	ifeq	L010
	getstatic	test4/b Z
	ifeq	L010
L009:
	getstatic	test4/h Ljava/lang/String;
	ldc	"Y"
	invokevirtual	java/lang/String.compareTo(Ljava/lang/String;)I
	ifeq	L013
	iconst_0
	goto	L014
L013:
	iconst_1
L014:
	ifeq	L010
	getstatic	test4/b Z
	ifeq	L010
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"%s\n"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	ldc	"!!! "
	aastore
	invokevirtual	java/io/PrintStream/printf(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;
	pop
	getstatic	test4/h Ljava/lang/String;
	ldc	"Y"
	invokevirtual	java/lang/String.compareTo(Ljava/lang/String;)I
	ifeq	L015
	iconst_0
	goto	L016
L015:
	iconst_1
L016:
	ifeq	L010
	getstatic	test4/b Z
	ifeq	L010
	iconst_0
	putstatic	test4/b Z
	getstatic	test4/h Ljava/lang/String;
	ldc	"Y"
	invokevirtual	java/lang/String.compareTo(Ljava/lang/String;)I
	ifeq	L017
	iconst_0
	goto	L018
L017:
	iconst_1
L018:
	ifeq	L010
	getstatic	test4/b Z
	ifeq	L010
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"%s\n"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	ldc	"?!?!?!"
	aastore
	invokevirtual	java/io/PrintStream/printf(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;
	pop
L010:
	getstatic	test4/n F
	ldc	5.0
	fcmpg
	ifeq	L019
	iconst_0
	goto	L020
L019:
	iconst_1
L020:
	ifeq	L002
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"%s\n"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	ldc	"World!"
	aastore
	invokevirtual	java/io/PrintStream/printf(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;
	pop
	getstatic	test4/n F
	ldc	5.0
	fcmpg
	ifeq	L021
	iconst_0
	goto	L022
L021:
	iconst_1
L022:
	ifeq	L002
	ldc	3.0
	putstatic	test4/n F
	getstatic	test4/n F
	ldc	5.0
	fcmpg
	ifeq	L023
	iconst_0
	goto	L024
L023:
	iconst_1
L024:
	ifeq	L002
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"%s\n"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	ldc	"!!!!!!!!"
	aastore
	invokevirtual	java/io/PrintStream/printf(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;
	pop
L002:

	invokestatic	java/time/Instant/now()Ljava/time/Instant;
	astore_2
	aload_1
	aload_2
	invokestatic	java/time/Duration/between(Ljava/time/temporal/Temporal;Ljava/time/temporal/Temporal;)Ljava/time/Duration;
	invokevirtual	java/time/Duration/toMillis()J
	lstore_3
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"\n[%,d milliseconds execution time.]\n"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	lload_3
	invokestatic	java/lang/Long/valueOf(J)Ljava/lang/Long;
	aastore
	invokevirtual	java/io/PrintStream/printf(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;
	pop

	return

.limit locals 16
.limit stack 16
.end method
