# OwODeleter

This discord bot is designed to delete messages containing variations of the phrase OwO, UwU, etc.

When a message is sent that is detected to contain a forbidden phrase, the message is deleted, and a message (from a list of random messages) is sent to the user.

A basic machine learning framework is used to attempt to detect text from image- see example here https://im2.ezgif.com/tmp/ezgif-2-85fabc2175cd.mp4

The bot also has administrator commands. The following features are available (to use a command, type "!!" followed by the command):

insult: Displays the requested insult by insult index. Parameters: Integer (index)

reinitialize: Reinitializes your choice of {admins}, {unacceptables}, or {insults} based on files. For example, if one were to manually
add an administrator to admins.txt, one could type "!!reinitialize admins" to make that change live.
Parameters: String {"unnaceptables", "admins", or "insults"}.

addAdmin: Adds the mentioned user to the admin list permanently. Parameters: String {Mention of the user}. Example: "!!addAdmin @evan"
Only a super admin can use the addAdmin command.

addCom: Adds the specified character to the banlist. Parameters:
character: the character that you'd like to ban. index: 0 for starting, 1 for transition,2 for middle. isEmote: true if is emote, false if not.
Example: "!addCom ú 0 true" will tell the bot that úwú is a forbidden term.

silence/unsilence: The bot will continue to moderate messages, but will keep quiet about it. Parameters: None.

lurk/unlurk: The bot will not moderate any messages. Parameters: None.
