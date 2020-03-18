# Bingo
A Minecraft Plugin for Spigot.  
Bingo is a survial game. You need to complete 5 out of 5x5=25 quests. And you need to complete them on a line.  
  
## Usage
- `/bingo gui` Open the Bingo GUI for starting, setting etc.  
- `/bingo start [settings]` Start a new Bingo game.   
- `/bingo stop` Stop the current game.  
- `/bingo setting <setting> <value>` Setting the game.  
- `/bingo join` Join the current game.  
- `/bingo leave` Leave the current game.  

## Config
settings.yml
```
yml
Config:
- '我还没做'
- '等着'
```

## Permission
- `bingo.admin` Allow you to run all the admin commands.
- `bingo.admin.gui` Allow you to run the command /bingo gui
- `bingo.admin.start` Allow you to run the command /bingo start
- `bingo.admin.stop` Allow you to run the command /bingo stop
- `bingo.admin.setting` Allow you to run the command /bingo setting
- `bingo.use` Allow you to run all the normal commands.
- `bingo.use.join` Allow you to run the command /bingo join
- `bingo.use.leave` Allow you to run the command /bingo leave
