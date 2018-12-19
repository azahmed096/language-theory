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
%part1 = alloca i32
%part2 = alloca i32
%part3 = alloca i32
%1 = call i32 @readInt()
store i32 %1, i32* %part1
%2 = call i32 @readInt()
store i32 %2, i32* %part2
%3 = call i32 @readInt()
store i32 %3, i32* %part3
%4 = load i32, i32* %part1
%5 = load i32, i32* %part2
%6 = add i32 %4, %5
%7 = load i32, i32* %part3
%8 = add i32 %6, %7
%9 = sdiv i32 %8, 3
call void @println(i32 %9)
%10 = load i32, i32* %part1
call void @println(i32 %10)
%11 = load i32, i32* %part2
call void @println(i32 %11)
%12 = load i32, i32* %part3
call void @println(i32 %12)
ret i32 0
}
