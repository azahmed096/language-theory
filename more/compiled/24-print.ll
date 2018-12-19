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
br label %begin0
begin0:
%2 = load i32, i32* %result
%3 = load i32, i32* %number
%4 = icmp slt i32 %2, %3
br i1 %4, label %inside0, label %outside0
br label %inside0
inside0:
%6 = load i32, i32* %result
call void @println(i32 %6)
%7 = load i32, i32* %result
%8 = add i32 %7, 1
store i32 %8, i32* %result
br label %begin0
br label %outside0
outside0:
ret i32 0
}
