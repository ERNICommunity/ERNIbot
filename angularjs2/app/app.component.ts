import { Component, OnInit, Input, OnChanges, SimpleChange, ChangeDetectionStrategy } from '@angular/core';

import { Message } from './message';
import { ChatService } from './chat.service';
import { Answer } from './chat.service';
import { Observable } from 'rxjs/Observable'
import { Subject } from 'rxjs/Subject'
import { BehaviorSubject } from "rxjs/Rx";
import 'rxjs/Rx';


// npm install angular2-scroll-glue@1.0.0

@Component({
  selector: 'my-app',
  template: `
    
    <h1>{{title}}</h1>
    <h2>Chat Verlauf</h2>
    
    <section class="chat">
      
      <div *ngFor="let message of chat" >
        <div [class.from-them]="message.user === 'bot'" [class.from-me]="message.user === 'human'">
          <p>{{message.text}}</p>
        </div>
        <div class="clear"></div>
      </div>
    </section>
    
    <input id="message" (keyup.enter)="onKey($event)">
  
  `,
  styles: [`
  @import "compass/css3";

  body {
  font-family: "Helvetica Neue";
  font-size: 20px;
  font-weight: normal;
}

#message {
  display: block;
  margin : 0 auto;
  width: 450px;
  border: 2px solid black;
  border-radius: 5px;
  font-family: "Helvetica Neue";
  font-size: 20px;
}
  
section {
  max-width: 450px;
  margin: 50px auto;
  height: 400px;
  
  border: 2px solid black;
  border-radius: 5px;
  
  overflow-y: scroll;
  overflow-x: hidden;
  
  div {
    max-width: 255px;
    word-wrap: break-word;
    margin-bottom: 20px;
    line-height: 24px;
  }
}

.clear {clear: both}
.from-me {
  position:relative;
  padding:10px 20px;
  color:white; 
  background:#0B93F6;
  border-radius:25px;
  float: right;
  
    
  &:before {
    content:"";
    position:absolute;
    z-index:-1;
    bottom:-2px;
    right:-7px;
    height:20px;
    border-right:20px solid #0B93F6;
    border-bottom-left-radius: 16px 14px;
    -webkit-transform:translate(0, -2px);
  }

  &:after {
    content:"";
    position:absolute;
    z-index:1;
    bottom:-2px;
    right:-56px;
    width:26px;
    height:20px;
    background:white;
    border-bottom-left-radius: 10px;
    -webkit-transform:translate(-30px, -2px);
  }
}
.from-them {
  position:relative;
  padding:10px 20px;
  background:#E5E5EA;
  border-radius:25px;
  color: black;
  float: left;
    
  &:before {
    content:"";
    position:absolute;
    z-index:2;
    bottom:-2px;
    left:-7px;
    height:20px;
    border-left:20px solid #E5E5EA;
    border-bottom-right-radius: 16px 14px;
    -webkit-transform:translate(0, -2px);
  }

  &:after {
    content:"";
    position:absolute;
    z-index:3;
    bottom:-2px;
    left:4px;
    width:26px;
    height:20px;
    background:white;
    border-bottom-right-radius: 10px;
    -webkit-transform:translate(-30px, -2px);
  }
  `],
  providers: [ChatService]

})

export class AppComponent implements OnInit {
  title = 'ERNIbot';
  chat: Message[];;

  constructor(private chatService: ChatService) { }

  ngOnInit(): void {

    this.chat = [];
  }

  onKey(event: any) {

    let humanMessage = new Message();
    humanMessage.text = event.target.value;
    humanMessage.user = "human";
    this.addMessage(humanMessage);

    event.target.value = "";

    let botMessage = new Message();
    botMessage.user = "bot";

    this.chatService.getResponse(humanMessage.text)
      .subscribe((data: Answer) => botMessage.text = data.responses[0],
      error => console.log(error),
      () => this.addMessage(botMessage),
    );

  }

  addMessage(message: Message) {
    this.chat.push(message)
    setTimeout(() => {
      this.scrollToBottom();
    })
  }

  scrollToBottom(): void {
    let result = document.getElementsByClassName("chat");

    result[0].scrollTop = 1E10;
    console.log("######" + result[0].scrollHeight)

  }
}
