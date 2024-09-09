.class public test3
.super java/lang/Object

.field private static _sysin Ljava/util/Scanner;

;
; Runtime input scanner
;
.method static <clinit>()V

	new	java/util/Scanner
	dup
	getstatic	java/lang/System/in Ljava/io/InputStream;
	invokespecial	java/util/Scanner/<init>(Ljava/io/InputStream;)V
	putstatic	test3/_sysin Ljava/util/Scanner;
	return

.limit locals 16
.limit stack 16
.end method

;
; Main class constructor
;
.method public <init>()V
.var 0 is this Ltest3;

	aload_0
	invokespecial	java/lang/Object/<init>()V
	return

.limit locals 16
.limit stack 16
.end method

;
; DEFINITION NO RETURN fibonacci
;
.method private static fibonacci(F)V

.var 1 is a F
.var 2 is b F
.var 0 is n F
.var 3 is next F
	fconst_0
	fstore_1
	fconst_1
	fstore_2
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"%s\n"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	ldc	"Fibonacci sequence up to"
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
	fload_0
	invokevirtual	java/lang/StringBuilder/append(F)Ljava/lang/StringBuilder;
	invokevirtual	java/lang/StringBuilder/toString()Ljava/lang/String;
	aastore
	invokevirtual	java/io/PrintStream/printf(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;
	pop
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	invokevirtual	java/io/PrintStream.println()V
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"%f\n"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	fload_1
	invokestatic	java/lang/Float/valueOf(F)Ljava/lang/Float;
	aastore
	invokevirtual	java/io/PrintStream/printf(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;
	pop
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"%f\n"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	fload_2
	invokestatic	java/lang/Float/valueOf(F)Ljava/lang/Float;
	aastore
	invokevirtual	java/io/PrintStream/printf(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;
	pop
	fconst_1
	fneg
	fstore_3
L001:
	fload_0
	fconst_2
	fcmpg
	ifgt	L003
	iconst_0
	goto	L004
L003:
	iconst_1
L004:
	ifeq	L002
	fload_1
	fload_2
	fadd
	fstore_3
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"%f\n"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	fload_3
	invokestatic	java/lang/Float/valueOf(F)Ljava/lang/Float;
	aastore
	invokevirtual	java/io/PrintStream/printf(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;
	pop
	fload_2
	fstore_1
	fload_3
	fstore_2
	fload_0
	fconst_1
	fsub
	fstore_0
	goto	L001
L002:

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

	ldc	10.0
	invokestatic	test3/fibonacci(F)V
	ldc	10.0

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
