.class public test1
.super java/lang/Object

.field private static _sysin Ljava/util/Scanner;
.field private static a Ljava/lang/String;
.field private static b Ljava/lang/String;
.field private static c Ljava/lang/String;
.field private static k F
.field private static x F
.field private static y F

;
; Runtime input scanner
;
.method static <clinit>()V

	new	java/util/Scanner
	dup
	getstatic	java/lang/System/in Ljava/io/InputStream;
	invokespecial	java/util/Scanner/<init>(Ljava/io/InputStream;)V
	putstatic	test1/_sysin Ljava/util/Scanner;
	return

.limit locals 16
.limit stack 16
.end method

;
; Main class constructor
;
.method public <init>()V
.var 0 is this Ltest1;

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
	putstatic	test1/x F
	ldc	6.0
	putstatic	test1/x F
	ldc	5.0
	fneg
	putstatic	test1/y F
	getstatic	test1/x F
	getstatic	test1/y F
	fadd
	putstatic	test1/k F
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"%f\n"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	getstatic	test1/k F
	invokestatic	java/lang/Float/valueOf(F)Ljava/lang/Float;
	aastore
	invokevirtual	java/io/PrintStream/printf(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;
	pop
	ldc	"Hello"
	putstatic	test1/a Ljava/lang/String;
	ldc	"World"
	putstatic	test1/b Ljava/lang/String;
	getstatic	test1/a Ljava/lang/String;
	new	java/lang/StringBuilder
	dup_x1
	swap
	invokestatic	java/lang/String/valueOf(Ljava/lang/Object;)Ljava/lang/String;
	invokespecial	java/lang/StringBuilder/<init>(Ljava/lang/String;)V
	ldc	" "
	invokevirtual	java/lang/StringBuilder/append(Ljava/lang/String;)Ljava/lang/StringBuilder;
	invokevirtual	java/lang/StringBuilder/toString()Ljava/lang/String;
	new	java/lang/StringBuilder
	dup_x1
	swap
	invokestatic	java/lang/String/valueOf(Ljava/lang/Object;)Ljava/lang/String;
	invokespecial	java/lang/StringBuilder/<init>(Ljava/lang/String;)V
	getstatic	test1/b Ljava/lang/String;
	invokevirtual	java/lang/StringBuilder/append(Ljava/lang/String;)Ljava/lang/StringBuilder;
	invokevirtual	java/lang/StringBuilder/toString()Ljava/lang/String;
	new	java/lang/StringBuilder
	dup_x1
	swap
	invokestatic	java/lang/String/valueOf(Ljava/lang/Object;)Ljava/lang/String;
	invokespecial	java/lang/StringBuilder/<init>(Ljava/lang/String;)V
	ldc	" "
	invokevirtual	java/lang/StringBuilder/append(Ljava/lang/String;)Ljava/lang/StringBuilder;
	invokevirtual	java/lang/StringBuilder/toString()Ljava/lang/String;
	new	java/lang/StringBuilder
	dup_x1
	swap
	invokestatic	java/lang/String/valueOf(Ljava/lang/Object;)Ljava/lang/String;
	invokespecial	java/lang/StringBuilder/<init>(Ljava/lang/String;)V
	getstatic	test1/x F
	invokevirtual	java/lang/StringBuilder/append(F)Ljava/lang/StringBuilder;
	invokevirtual	java/lang/StringBuilder/toString()Ljava/lang/String;
	putstatic	test1/c Ljava/lang/String;
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"%s\n"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	getstatic	test1/c Ljava/lang/String;
	aastore
	invokevirtual	java/io/PrintStream/printf(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;
	pop

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

