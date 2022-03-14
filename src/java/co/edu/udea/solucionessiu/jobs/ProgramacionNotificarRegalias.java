/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.udea.solucionessiu.jobs;

import co.edu.udea.solucionessiu.exception.GIDaoException;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;

/**
 *
 * @author jorge.correa
 */
public class ProgramacionNotificarRegalias {
    
    public void crearProgramacion(){
        
        Integer intError=0;
        
        try {
            
            new GIDaoException("Inicio la programación de la tarea NotificarRegalias");
                                        
            intError = 1;
            
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start(); 
            
            // Creación de una instancia de JobDetail.
            JobDetail jobDetail1 = new JobDetail("NotificarRegaliasJob1", scheduler.DEFAULT_GROUP, NotificarRegalias.class);
            JobDetail jobDetail2 = new JobDetail("NotificarRegaliasJob2", scheduler.DEFAULT_GROUP, NotificarRegalias.class);
            
             intError = 2;
            
            // Se crea el trigger para ejecución días 9 de cada mes.
             Trigger trigger1 = TriggerUtils.makeMonthlyTrigger(9, 7, 30);     
             trigger1.setName("tgNotificarRegalias1");
             trigger1.setGroup("grupoSIGEP");
             trigger1.setPriority(2);     
             
              intError = 3;
             
             // Se crea el trigger para ejecución días 10 de cada mes.
             Trigger trigger2 = TriggerUtils.makeMonthlyTrigger(10, 7, 30);     
             trigger2.setName("tgNotificarRegalias2");
             trigger2.setGroup("grupoSIGEP");
             trigger2.setPriority(2); 
                                                  
             intError = 4;

            // Registro dentro del Scheduler.            
            scheduler.scheduleJob(jobDetail1, trigger1);
            scheduler.scheduleJob(jobDetail2, trigger2);
            
             intError = 5;

             new GIDaoException("Finalizó la programación de la tarea NotificarRegalias.");

        } catch(Exception e) {
            new GIDaoException("Se generó un error al ejecutar la tarea NotificarRegalias. Variable error = " +  intError, e);
        }
    } 
}
