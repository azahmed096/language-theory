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
%e = alloca i32
%1 = call i32 @readInt()
store i32 %1, i32* %a
%2 = call i32 @readInt()
store i32 %2, i32* %b
%3 = call i32 @readInt()
store i32 %3, i32* %c
%4 = load i32, i32* %a
%5 = load i32, i32* %a
%6 = mul i32 %4, %5
%7 = load i32, i32* %b
%8 = load i32, i32* %b
%9 = mul i32 %7, %8
%10 = add i32 %6, %9
%11 = load i32, i32* %c
%12 = load i32, i32* %c
%13 = mul i32 %11, %12
%14 = sub i32 %10, %13
store i32 %14, i32* %e
%15 = load i32, i32* %a
%16 = icmp eq i32 %15, 0
%17 = load i32, i32* %b
%18 = icmp eq i32 %17, 0
%19 = or i1 %16, %18
%20 = load i32, i32* %c
%21 = icmp eq i32 %20, 0
%22 = or i1 %19, %21
br i1 %22, label %true0, label %false0
br label %true0
true0:
%24 = sdiv i32 8, 2
call void @println(i32 %24)
br label %end0
br label %false0
false0:
br label %end0
end0:
%26 = load i32, i32* %e
call void @println(i32 %26)
%27 = load i32, i32* %a
%28 = load i32, i32* %b
%29 = icmp sgt i32 %27, %28
%30 = load i32, i32* %b
%31 = load i32, i32* %c
%32 = icmp sgt i32 %30, %31
%33 = and i1 %29, %32
br i1 %33, label %true1, label %false1
br label %true1
true1:
%35 = load i32, i32* %a
call void @println(i32 %35)
%36 = load i32, i32* %b
call void @println(i32 %36)
%37 = load i32, i32* %c
call void @println(i32 %37)
br label %end1
br label %false1
false1:
%39 = load i32, i32* %a
%40 = load i32, i32* %b
%41 = icmp sgt i32 %39, %40
%42 = load i32, i32* %b
%43 = load i32, i32* %c
%44 = icmp sgt i32 %42, %43
%45 = and i1 %41, %44
br i1 %45, label %true2, label %false2
br label %true2
true2:
%47 = sub i32 0, 1
call void @println(i32 %47)
br label %end2
br label %false2
false2:
%49 = add i32 2, 1
%50 = icmp slt i32 1, %49
%incrementer_3 = alloca i32
%i = alloca i32
store i32 1, i32* %i
br i1 %50, label %positive3, label %negative3
br label %positive3
positive3:
store i32 1, i32* %incrementer_3
br label %begin3
br label %negative3
negative3:
store i32 -1, i32* %incrementer_3
br label %begin3
begin3:
br label %compare3
compare3:
%53 = load i32, i32* %i
%54 = icmp eq i32 %53, %49
br i1 %54, label %outside3, label %inside3
br label %inside3
inside3:
%56 = load i32, i32* %b
%57 = load i32, i32* %c
%58 = icmp slt i32 %56, %57
br i1 %58, label %true4, label %false4
br label %true4
true4:
%60 = load i32, i32* %b
store i32 %60, i32* %temp
%61 = load i32, i32* %c
store i32 %61, i32* %b
%62 = load i32, i32* %temp
store i32 %62, i32* %c
br label %end4
br label %false4
false4:
br label %end4
end4:
%64 = load i32, i32* %a
%65 = load i32, i32* %b
%66 = icmp slt i32 %64, %65
br i1 %66, label %true5, label %false5
br label %true5
true5:
%68 = load i32, i32* %a
store i32 %68, i32* %temp
%69 = load i32, i32* %b
store i32 %69, i32* %a
%70 = load i32, i32* %temp
store i32 %70, i32* %b
br label %end5
br label %false5
false5:
br label %end5
end5:
%72 = load i32, i32* %incrementer_3
%73 = load i32, i32* %i
%74 = add i32 %73, %72
store i32 %74, i32* %i
br label %compare3
br label %outside3
outside3:
br label %end2
end2:
%76 = load i32, i32* %a
call void @println(i32 %76)
%77 = load i32, i32* %b
call void @println(i32 %77)
%78 = load i32, i32* %c
call void @println(i32 %78)
br label %end1
end1:
ret i32 0
}
