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
%number = alloca i32
%power = alloca i32
%result = alloca i32
%1 = call i32 @readInt()
store i32 %1, i32* %number
%2 = call i32 @readInt()
store i32 %2, i32* %power
store i32 1, i32* %result
%3 = load i32, i32* %power
%4 = icmp slt i32 1, %3
%i_0 = alloca i32
%$increment_0 = alloca i32
store i32 1, i32* %i_0
br i1 %4, label %positive0, label %negative0
br label %positive0
positive0:
store i32 1, i32* %$increment_0
br label %begin0
br label %negative0
negative0:
store i32 -1, i32* %$increment_0
br label %begin0
begin0:
%7 = load i32, i32* %$increment_0
%8 = add i32 %3, %7
br label %compare0
compare0:
%9 = load i32, i32* %i_0
%10 = icmp eq i32 %9, %8
br i1 %10, label %outside0, label %inside0
br label %inside0
inside0:
%12 = load i32, i32* %number
%13 = load i32, i32* %result
%14 = mul i32 %12, %13
store i32 %14, i32* %result
%15 = load i32, i32* %i_0
%16 = load i32, i32* %$increment_0
%17 = add i32 %15, %16
store i32 %17, i32* %i_0
br label %compare0
br label %outside0
outside0:
%19 = load i32, i32* %result
call void @println(i32 %19)
ret i32 0
}
