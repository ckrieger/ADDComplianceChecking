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
  MatOptionModule, MatSelectModule, MatSnackBarModule,
  MatToolbarModule,
  MatTooltipModule
} from '@angular/material';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { PatternEditComponent } from './pattern-edit/pattern-edit.component';
import { ProjectListComponent } from './project-list/project-list.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MonitoringAreaComponent } from './monitoring-area/monitoring-area.component';
import { AddPatternDialogComponent } from './add-pattern-dialog/add-pattern-dialog.component';
import { InjectableRxStompConfig, RxStompService, rxStompServiceFactory } from '@stomp/ng2-stompjs';
import { rxStompConfig } from './rx-stomp.config';
import { HashLocationStrategy, LocationStrategy } from '@angular/common';
import { InstrumentationTemplateOverviewComponent } from './instrumentation-template-overview/instrumentation-template-overview.component';
import { TemplateEditComponent } from './template-edit/template-edit.component';
import { MatChipsModule } from '@angular/material/chips';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { SortByPipe } from './pipes/sort-by.pipe';
import { EventTypesOverviewComponent } from './event-types-overview/event-types-overview.component';
import { MatTableModule } from '@angular/material/table';
import { EventTypeEditComponent } from './event-type-edit/event-type-edit.component';
import hljs from 'highlight.js/lib/highlight';
import javascript from 'highlight.js/lib/languages/javascript';
import typescript from 'highlight.js/lib/languages/typescript';
import { AngularHighlightJsModule } from 'angular2-highlight-js';

hljs.registerLanguage('typescript', typescript);
hljs.registerLanguage('javascript', javascript);

@NgModule({
  declarations: [
    AppComponent,
    PatternComponent,
    PatternEditComponent,
    ProjectListComponent,
    MonitoringAreaComponent,
    AddPatternDialogComponent,
    InstrumentationTemplateOverviewComponent,
    TemplateEditComponent,
    SortByPipe,
    EventTypesOverviewComponent,
    EventTypeEditComponent
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
        MatIconModule,
        MatTooltipModule,
        MatSnackBarModule,
        MatChipsModule,
        ReactiveFormsModule,
        MatAutocompleteModule,
        MatTableModule,
        AngularHighlightJsModule
    ],
  providers: [
    {
      provide: InjectableRxStompConfig,
      useValue: rxStompConfig
    },
    {
      provide: RxStompService,
      useFactory: rxStompServiceFactory,
      deps: [InjectableRxStompConfig]
    },
    {
      provide: LocationStrategy,
      useClass: HashLocationStrategy
    }
  ],
  bootstrap: [AppComponent],
  entryComponents: [AddPatternDialogComponent]
})
export class AppModule { }
