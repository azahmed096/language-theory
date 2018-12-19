import os.path


C = "java -jar dist/part3.jar %s>%s"


def name(file):
    assert file.endswith(".sf")
    return file[:-3] + ".ll"


TEST_FOLDER = os.path.abspath("./test")
COMPILE_FOLDER = os.path.abspath("./compiled")
FILES = os.listdir(TEST_FOLDER)


for f in FILES:
    in_ = os.path.join(TEST_FOLDER, f)
    out_ = os.path.join(COMPILE_FOLDER, name(f))
    k = os.system(C % (
        in_,
        out_
    ))
    if k != 0 or os.stat(out_).st_size == 0:
        os.remove(out_)
