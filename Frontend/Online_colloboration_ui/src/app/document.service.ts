import { Injectable } from '@angular/core';;
import { Observable } from 'rxjs';
import { webSocket,WebSocketSubject } from 'rxjs/webSocket';
@Injectable({
  providedIn: 'root'
})
export class DocumentService {

  private socket: WebSocketSubject<any>;

constructor() {
  this.socket = webSocket('ws://localhost:8080');
}

updateDocument(documentContent: string): void {
  this.socket.next({ action: 'update', content: documentContent });
}
}
