import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { PatternComponent } from './pattern/pattern.component';
import { PatternEditComponent } from './pattern-edit/pattern-edit.component';
import { MonitoringAreaComponent } from './monitoring-area/monitoring-area.component';
import { InstrumentationTemplateOverviewComponent } from './instrumentation-template-overview/instrumentation-template-overview.component';
import { TemplateEditComponent } from './template-edit/template-edit.component';
import { EventTypesOverviewComponent } from './event-types-overview/event-types-overview.component';
import { EventTypeEditComponent } from './event-type-edit/event-type-edit.component';

const routes: Routes = [
  {path: '', redirectTo: 'home', pathMatch: 'full'},
  {
    path:'home',
    component: MonitoringAreaComponent
  },
  {
    path: 'pattern-list',
    component: PatternComponent
  },
  {
    path: 'pattern-edit',
    component: PatternEditComponent
  },
  {
    path: 'pattern-edit/:id',
    component: PatternEditComponent
  },
  {
    path: 'template-list',
    component: InstrumentationTemplateOverviewComponent
  },
  {
    path: 'template-edit',
    component: TemplateEditComponent
  },
  {
    path: 'template-edit:id',
    component: TemplateEditComponent
  },
  {
    path: 'event-type-list',
    component: EventTypesOverviewComponent
  },
  {
    path: 'event-type-edit',
    component: EventTypeEditComponent
  },
  {
    path: 'event-type-edit/:id',
    component: EventTypeEditComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
