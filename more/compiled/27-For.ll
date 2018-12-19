@.strR = private unnamed_addr constant [3 x i8] c"%d\00", align 1
define i32 @readInt() {
  %x = alloca i32, align 4
  %1 = call i32 (i8*, ...) @__isoc99_scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.strR, i32 0, i32 0), i32* %x)
  %2 = load i32, i32* %x, align 4
  ret i32 %2
}
declare i32 @__isoc99_scanf(i8*, ...)
@.strP = private unnamed_addr constant [4 x i8] c"%d\0A\00", align 1
define void @println(i32 %x) {
  %1 = alloca i32, align 4
  store i32 %x, i32* %1, align 4
  %2 = load i32, i32* %1, align 4
  %3 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.strP, i32 0, i32 0), i32 %2)
  ret void
}
declare i32 @printf(i8*, ...)
define i32 @main(){
%x = alloca i32
store i32 42, i32* %x
%1 = icmp slt i32 0, 5
%x_0 = alloca i32
%$increment_0 = alloca i32
store i32 0, i32* %x_0
br i1 %1, label %positive0, label %negative0
br label %positive0
positive0:
store i32 1, i32* %$increment_0
br label %begin0
br label %negative0
negative0:
store i32 -1, i32* %$increment_0
br label %begin0
begin0:
%4 = load i32, i32* %$increment_0
%5 = add i32 5, %4
br label %compare0
compare0:
%6 = load i32, i32* %x_0
%7 = icmp eq i32 %6, %5
br i1 %7, label %outside0, label %inside0
br label %inside0
inside0:
%9 = icmp slt i32 0, 5
%x_1 = alloca i32
%$increment_1 = alloca i32
store i32 0, i32* %x_1
br i1 %9, label %positive1, label %negative1
br label %positive1
positive1:
store i32 1, i32* %$increment_1
br label %begin1
br label %negative1
negative1:
store i32 -1, i32* %$increment_1
br label %begin1
begin1:
%12 = load i32, i32* %$increment_1
%13 = add i32 5, %12
br label %compare1
compare1:
%14 = load i32, i32* %x_1
%15 = icmp eq i32 %14, %13
br i1 %15, label %outside1, label %inside1
br label %inside1
inside1:
%17 = load i32, i32* %x_1
call void @println(i32 %17)
%18 = load i32, i32* %x_1
%19 = load i32, i32* %$increment_1
%20 = add i32 %18, %19
store i32 %20, i32* %x_1
br label %compare1
br label %outside1
outside1:
%22 = load i32, i32* %x_0
%23 = load i32, i32* %$increment_0
%24 = add i32 %22, %23
store i32 %24, i32* %x_0
br label %compare0
br label %outside0
outside0:
%26 = load i32, i32* %x
call void @println(i32 %26)
ret i32 0
}
