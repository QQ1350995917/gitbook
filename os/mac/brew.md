# brew切换镜像源

## brew使用国内镜像源
```bash
cd "$(brew --repo)"
git remote set-url origin https://mirrors.tuna.tsinghua.edu.cn/git/homebrew/brew.git

cd "$(brew --repo)/Library/Taps/homebrew/homebrew-core"
git remote set-url origin https://mirrors.tuna.tsinghua.edu.cn/git/homebrew/homebrew-core.git

brew update
``` 

## 复原
```bash
cd "$(brew --repo)"
git remote set-url origin https://github.com/Homebrew/brew.git
 
cd "$(brew --repo)/Library/Taps/homebrew/homebrew-core"
git remote set-url origin https://github.com/Homebrew/homebrew-core
 
brew update
```

## 备用源
```bash
git remote set-url origin git://mirrors.ustc.edu.cn/brew.git
git remote set-url origin git://mirrors.ustc.edu.cn/homebrew-core.git


```
