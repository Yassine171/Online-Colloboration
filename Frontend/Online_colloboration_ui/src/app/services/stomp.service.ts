import { Injectable } from '@angular/core';
import * as SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';
@Injectable({
  providedIn: 'root'
})
export class StompService {


  constructor() { }


  socket=new SockJS('http://localhost:8081/websocket-docs');
  stompClient=Stomp.over(this.socket);

  subscribe(topic:string, callback:any):void{
    const connected :boolean = this.stompClient.connected;
    if(connected){
      this.subscribeToTopic(topic,callback);
      return;
    }

    this.stompClient.connect({},():any=>{
      this.subscribeToTopic(topic,callback);
    })
  }

  private subscribeToTopic(topic:string,callback:any):void {
    this.stompClient.subscribe(topic,():any=>{
      callback();
    });
  }


  sendMessage(destination: string, body: any, headers?: any): void {
    if (!this.stompClient.connected) {
      // If not connected, attempt to connect
      this.stompClient.connect({}, () => {
        // After connecting, send the message
        this.stompClient.send(destination, headers || {}, body);
      });
    } else {
      // If already connected, send the message
      this.stompClient.send(destination, headers || {}, body);
    }
  }
}
