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
%result = alloca i32
%1 = call i32 @readInt()
store i32 %1, i32* %number
store i32 1, i32* %result
%2 = load i32, i32* %number
%3 = icmp sge i32 %2, 0
br i1 %3, label %true0, label %false0
br label %true0
true0:
%5 = load i32, i32* %number
%6 = icmp slt i32 1, %5
%i_0 = alloca i32
%$increment_0 = alloca i32
store i32 1, i32* %i_0
br i1 %6, label %positive1, label %negative1
br label %positive1
positive1:
store i32 1, i32* %$increment_0
br label %begin1
br label %negative1
negative1:
store i32 -1, i32* %$increment_0
br label %begin1
begin1:
%9 = load i32, i32* %$increment_0
%10 = add i32 %5, %9
br label %compare1
compare1:
%11 = load i32, i32* %i_0
%12 = icmp eq i32 %11, %10
br i1 %12, label %outside1, label %inside1
br label %inside1
inside1:
%14 = load i32, i32* %result
%15 = load i32, i32* %i_0
%16 = mul i32 %14, %15
store i32 %16, i32* %result
%17 = load i32, i32* %i_0
%18 = load i32, i32* %$increment_0
%19 = add i32 %17, %18
store i32 %19, i32* %i_0
br label %compare1
br label %outside1
outside1:
%21 = load i32, i32* %result
call void @println(i32 %21)
br label %end0
br label %false0
false0:
%23 = sub i32 0, 1
call void @println(i32 %23)
br label %end0
end0:
ret i32 0
}
