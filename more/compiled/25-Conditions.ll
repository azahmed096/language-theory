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
%a = alloca i32
%b = alloca i32
%c = alloca i32
%temp = alloca i32
%1 = call i32 @readInt()
store i32 %1, i32* %a
%2 = call i32 @readInt()
store i32 %2, i32* %b
%3 = call i32 @readInt()
store i32 %3, i32* %c
%4 = load i32, i32* %c
%5 = load i32, i32* %c
%6 = add i32 %4, %5
%7 = mul i32 %6, 4
%8 = load i32, i32* %c
%9 = mul i32 7, %8
%10 = sub i32 %7, %9
store i32 %10, i32* %c
%11 = load i32, i32* %a
%12 = load i32, i32* %b
%13 = icmp sgt i32 %11, %12
%14 = load i32, i32* %b
%15 = load i32, i32* %c
%16 = icmp sgt i32 %14, %15
%17 = and i1 %13, %16
br i1 %17, label %true0, label %false0
br label %true0
true0:
%19 = load i32, i32* %a
call void @println(i32 %19)
%20 = load i32, i32* %b
call void @println(i32 %20)
%21 = load i32, i32* %c
call void @println(i32 %21)
br label %end0
br label %false0
false0:
%23 = load i32, i32* %a
%24 = load i32, i32* %b
%25 = icmp sgt i32 %23, %24
%26 = load i32, i32* %b
%27 = load i32, i32* %c
%28 = icmp sgt i32 %26, %27
%29 = and i1 %25, %28
br i1 %29, label %true1, label %false1
br label %true1
true1:
%31 = sub i32 0, 1
call void @println(i32 %31)
br label %end1
br label %false1
false1:
%33 = add i32 2, 1
%34 = icmp slt i32 1, %33
%i_0 = alloca i32
%$increment_0 = alloca i32
store i32 1, i32* %i_0
br i1 %34, label %positive2, label %negative2
br label %positive2
positive2:
store i32 1, i32* %$increment_0
br label %begin2
br label %negative2
negative2:
store i32 -1, i32* %$increment_0
br label %begin2
begin2:
%37 = load i32, i32* %$increment_0
%38 = add i32 %33, %37
br label %compare2
compare2:
%39 = load i32, i32* %i_0
%40 = icmp eq i32 %39, %38
br i1 %40, label %outside2, label %inside2
br label %inside2
inside2:
%42 = load i32, i32* %b
%43 = load i32, i32* %c
%44 = icmp slt i32 %42, %43
br i1 %44, label %true3, label %false3
br label %true3
true3:
%46 = load i32, i32* %b
store i32 %46, i32* %temp
%47 = load i32, i32* %c
store i32 %47, i32* %b
%48 = load i32, i32* %temp
store i32 %48, i32* %c
br label %end3
br label %false3
false3:
br label %end3
end3:
%50 = load i32, i32* %a
%51 = load i32, i32* %b
%52 = icmp slt i32 %50, %51
br i1 %52, label %true4, label %false4
br label %true4
true4:
%54 = load i32, i32* %a
store i32 %54, i32* %temp
%55 = load i32, i32* %b
store i32 %55, i32* %a
%56 = load i32, i32* %temp
store i32 %56, i32* %b
br label %end4
br label %false4
false4:
br label %end4
end4:
%58 = load i32, i32* %i_0
%59 = load i32, i32* %$increment_0
%60 = add i32 %58, %59
store i32 %60, i32* %i_0
br label %compare2
br label %outside2
outside2:
br label %end1
end1:
%62 = load i32, i32* %a
call void @println(i32 %62)
%63 = load i32, i32* %b
call void @println(i32 %63)
%64 = load i32, i32* %c
call void @println(i32 %64)
br label %end0
end0:
ret i32 0
}
