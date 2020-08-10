import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class EventService {

  constructor(private httpClient: HttpClient) { }

  getAll(){
    return this.httpClient.get<any[]>(environment.apiUrl + 'events/events');
  }

}
