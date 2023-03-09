import { WebSocketSubject } from 'rxjs/webSocket';
import { DocumentService } from './../../document.service';

import { Component, OnDestroy, OnInit } from '@angular/core';
import { Quill } from 'quill';
import { ActivatedRoute } from '@angular/router';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import Delta from 'quill-delta';

@Component({
  selector: 'app-edit-document',
  templateUrl: './edit-document.component.html',
  styleUrls: ['./edit-document.component.css']
})
export class EditDocumentComponent implements OnInit , OnDestroy {
  private readonly destroy$ = new Subject<void>();
  private readonly documentId: number;
  private socket!: WebSocketSubject<any>;
  public editor!: Quill;
  public documentContent!: string;

  constructor(private route: ActivatedRoute) {
    this.documentId = this.route.snapshot.params['id'];
  }
  ngOnInit(): void {
    this.socket = new WebSocketSubject<any>('ws://localhost:8080/document/' + this.documentId);

    // Subscribe to updates from the server
    this.socket.pipe(
      takeUntil(this.destroy$)
    ).subscribe(
      (message) => {
        if (message.action === 'update') {
          this.editor.updateContents(message.content, 'api');
        }
      },
      (error) => {
        console.error(error);
      }
    );

    // Initialize the Quill editor
    this.editor = new Quill('#editor', {
      theme: 'snow',
      modules: {
        toolbar: [
          ['bold', 'italic', 'underline', 'strike'],
          [{ 'header': 1 }, { 'header': 2 }],
          [{ 'list': 'ordered' }, { 'list': 'bullet' }],
          [{ 'indent': '-1' }, { 'indent': '+1' }],
          [{ 'align': [] }],
          ['link', 'image', 'video'],
          ['clean']
        ]
      }
    });

    // Subscribe to editor changes and send them to the server
    this.editor.on('text-change', (delta, oldDelta, source) => {
      if (source === 'user') {
        const content = this.editor.getContents();
        this.socket.next({ action: 'update', content: content });
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.socket.complete();
  }
}
