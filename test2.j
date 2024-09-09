.class public test2
.super java/lang/Object

.field private static _sysin Ljava/util/Scanner;
.field private static i F

;
; Runtime input scanner
;
.method static <clinit>()V

	new	java/util/Scanner
	dup
	getstatic	java/lang/System/in Ljava/io/InputStream;
	invokespecial	java/util/Scanner/<init>(Ljava/io/InputStream;)V
	putstatic	test2/_sysin Ljava/util/Scanner;
	return

.limit locals 16
.limit stack 16
.end method

;
; Main class constructor
;
.method public <init>()V
.var 0 is this Ltest2;

	aload_0
	invokespecial	java/lang/Object/<init>()V
	return

.limit locals 16
.limit stack 16
.end method

;
; DEFINITION increment
;
.method private static increment(F)F

.var 2 is increment F
.var 1 is newnum F
.var 0 is oldnum F
	fload_0
	fconst_1
	fadd
	fstore_1

	fload_1
	fstore_2
	fload_2
	freturn

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

	fconst_0
	putstatic	test2/i F
L001:
	getstatic	test2/i F
	ldc	10.0
	fcmpg
	ifle	L003
	iconst_0
	goto	L004
L003:
	iconst_1
L004:
	ifeq	L002
	getstatic	test2/i F
	ldc	3.0
	fcmpg
	iflt	L006
	iconst_0
	goto	L007
L006:
	iconst_1
L007:
	ifeq	L008
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"%s\n"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	ldc	"i < 3 where i = "
	new	java/lang/StringBuilder
	dup_x1
	swap
	invokestatic	java/lang/String/valueOf(Ljava/lang/Object;)Ljava/lang/String;
	invokespecial	java/lang/StringBuilder/<init>(Ljava/lang/String;)V
	getstatic	test2/i F
	invokevirtual	java/lang/StringBuilder/append(F)Ljava/lang/StringBuilder;
	invokevirtual	java/lang/StringBuilder/toString()Ljava/lang/String;
	aastore
	invokevirtual	java/io/PrintStream/printf(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;
	pop
	goto	L005
L008:
	getstatic	test2/i F
	ldc	5.0
	fcmpg
	iflt	L009
	iconst_0
	goto	L010
L009:
	iconst_1
L010:
	ifeq	L011
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"%s\n"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	ldc	"i < 6 where i = "
	new	java/lang/StringBuilder
	dup_x1
	swap
	invokestatic	java/lang/String/valueOf(Ljava/lang/Object;)Ljava/lang/String;
	invokespecial	java/lang/StringBuilder/<init>(Ljava/lang/String;)V
	getstatic	test2/i F
	invokevirtual	java/lang/StringBuilder/append(F)Ljava/lang/StringBuilder;
	invokevirtual	java/lang/StringBuilder/toString()Ljava/lang/String;
	aastore
	invokevirtual	java/io/PrintStream/printf(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;
	pop
	goto	L005
L011:
	getstatic	test2/i F
	ldc	8.0
	fcmpg
	iflt	L012
	iconst_0
	goto	L013
L012:
	iconst_1
L013:
	ifeq	L014
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"%s\n"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	ldc	"i < 8 where i = "
	new	java/lang/StringBuilder
	dup_x1
	swap
	invokestatic	java/lang/String/valueOf(Ljava/lang/Object;)Ljava/lang/String;
	invokespecial	java/lang/StringBuilder/<init>(Ljava/lang/String;)V
	getstatic	test2/i F
	invokevirtual	java/lang/StringBuilder/append(F)Ljava/lang/StringBuilder;
	invokevirtual	java/lang/StringBuilder/toString()Ljava/lang/String;
	aastore
	invokevirtual	java/io/PrintStream/printf(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;
	pop
	goto	L005
L014:
	getstatic	java/lang/System/out Ljava/io/PrintStream;
	ldc	"%s\n"
	iconst_1
	anewarray	java/lang/Object
	dup
	iconst_0
	ldc	"i <= 10 where i = "
	new	java/lang/StringBuilder
	dup_x1
	swap
	invokestatic	java/lang/String/valueOf(Ljava/lang/Object;)Ljava/lang/String;
	invokespecial	java/lang/StringBuilder/<init>(Ljava/lang/String;)V
	getstatic	test2/i F
	invokevirtual	java/lang/StringBuilder/append(F)Ljava/lang/StringBuilder;
	invokevirtual	java/lang/StringBuilder/toString()Ljava/lang/String;
	aastore
	invokevirtual	java/io/PrintStream/printf(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;
	pop
L005:
	getstatic	test2/i F
	invokestatic	test2/increment(F)F
	putstatic	test2/i F
	goto	L001
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
