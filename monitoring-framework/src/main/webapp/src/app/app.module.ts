import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HttpClientModule } from '@angular/common/http';
import { PatternComponent } from './pattern/pattern.component';
import {
  MatButtonModule,
  MatCardModule, MatDialogModule,
  MatExpansionModule, MatIconModule,
  MatInputModule,
  MatListModule,
  MatOptionModule, MatSelectModule,
  MatToolbarModule
} from '@angular/material';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { PatternEditComponent } from './pattern-edit/pattern-edit.component';
import { ProjectListComponent } from './project-list/project-list.component';
import { FormsModule } from '@angular/forms';
import { MonitoringAreaComponent } from './monitoring-area/monitoring-area.component';
import { AddPatternDialogComponent } from './add-pattern-dialog/add-pattern-dialog.component';

@NgModule({
  declarations: [
    AppComponent,
    PatternComponent,
    PatternEditComponent,
    ProjectListComponent,
    MonitoringAreaComponent,
    AddPatternDialogComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    BrowserAnimationsModule,
    MatButtonModule,
    MatCardModule,
    MatInputModule,
    MatListModule,
    MatToolbarModule,
    FormsModule,
    MatExpansionModule,
    MatOptionModule,
    MatSelectModule,
    MatDialogModule,
    MatIconModule
  ],
  providers: [],
  bootstrap: [AppComponent],
  entryComponents: [AddPatternDialogComponent]
})
export class AppModule { }
