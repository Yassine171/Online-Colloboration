import { AuthService } from './../auth/shared/auth.service';
import { Injectable } from '@angular/core';
import * as SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';

@Injectable({
  providedIn: 'root'
})
export class StompService {

  constructor(private authService: AuthService) { }

  socket = new SockJS('http://localhost:8081/websocket-docs');
  stompClient = Stomp.over(this.socket);

  subscribe(topic: string, callback: any): void {
    const connected: boolean = this.stompClient.connected;
    if (connected) {
      this.subscribeToTopic(topic, callback);
      return;
    }

    this.stompClient.connect({
      headers: {
        'username': this.authService.getUserName(),
        'Authorization': 'Bearer ' + this.authService.getJwtToken()
      }
    }, () => {
      this.subscribeToTopic(topic, callback);
    });
  }

  private subscribeToTopic(topic: string, callback: any): void {
    this.stompClient.subscribe(topic, () => {
      callback();
    });
  }

  sendMessage(destination: string, body: any, headers?: any): void {
    if (!this.stompClient.connected) {
      // If not connected, attempt to connect
      this.stompClient.connect({
        headers: {
          'username': this.authService.getUserName(),
          'Authorization': 'Bearer ' + this.authService.getJwtToken()
        }
      }, () => {
        // After connecting, send the message
        headers = this.addTokenHeader(headers);
        this.stompClient.send(destination, headers || {}, body);
      });
    } else {
      // If already connected, send the message
      headers = this.addTokenHeader(headers);
      this.stompClient.send(destination, headers || {}, body);
    }
  }

  private addTokenHeader(headers: any): any {
    const token = this.authService.getJwtToken();
    if (token) {
      if (!headers) {
        headers = {};
      }
      headers['Authorization'] = 'Bearer ' + token;
    }
    return headers;
  }
}
