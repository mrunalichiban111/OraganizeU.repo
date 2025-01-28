class TaskManager {
    constructor() {
        this.tasks = [];
        this.initialize();
    }

    initialize() {
        this.addTask(
            "Website Redesign",
            "John Doe",
            "2025-02-15",
            "high",
            "pending"
        );
        this.addTask(
            "Database Migration",
            "Jane Smith",
            "2025-02-20",
            "medium",
            "completed"
        );
    }

    addTask(taskName, assignedTo, dueDate, priority, status) {
        const task = {
            taskName,
            taskId: 'T-' + Math.floor(Math.random() * 100000),
            assignedTo,
            dueDate,
            priority,
            status
        };
        this.tasks.unshift(task);
        this.renderTasks();
        return task;
    }

    renderTasks() {
        const tableBody = document.getElementById('taskTableBody');
        tableBody.innerHTML = '';

        this.tasks.forEach(task => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${task.taskName}</td>
                <td>${task.taskId}</td>
                <td>${task.assignedTo}</td>
                <td>${task.dueDate}</td>
                <td><span class="priority-${task.priority}">${task.priority}</span></td>
                <td><span class="status-${task.status}">${task.status}</span></td>
                <td><a href="#" class="details-link">Details</a></td>
            `;
            tableBody.appendChild(row);
        });
    }
}

let taskManager;
document.addEventListener('DOMContentLoaded', () => {
    taskManager = new TaskManager();
});

function addNewTask(taskName, assignedTo, dueDate, priority, status) {
    if (taskManager) {
        return taskManager.addTask(taskName, assignedTo, dueDate, priority, status);
    }
    return null;
}
setTimeout(() => {
    addNewTask("Wake Up", "Me", "12-02-2003", "high", "pending");
    addNewTask("Brush", "Me", "12-02-2003", "low", "completed");
    addNewTask("Breakfast", "Me", "12-02-2003", "high", "high", "pending");
}, 100);

function removeGoal(button) {
    const goalItem = button.parentElement;
    if (goalItem) {
      goalItem.remove();
    } else {
      console.error('Goal item not found');
    }
  }

  const themeToggler=document.querySelector("themeToggler");
  themeToggler.addEventListener('click',()=>{
      document.body.classList.toggle('dark-theme-variables');
      themeToggler.querySelector('span:nth-child(1)').classList.toggle('active');
      themeToggler.querySelector('span:nth-child(2)').classList.toggle('active');
  })
  
  document.addEventListener('DOMContentLoaded', () => {
      const dropdownButton = document.getElementById('dropdownMenuButton');
      const dropdownItems = document.querySelectorAll('.dropdown-item');
  
      
      dropdownItems.forEach(item => {
        item.addEventListener('click', (e) => {
          e.preventDefault(); 
          const selectedValue = item.getAttribute('data-value');
          dropdownButton.textContent = selectedValue; 
        });
      });
    });

    let tasks = [];

const new_task = () => {
    const task_input = document.getElementById("enter_task");
    const task_text = task_input.value.trim();

    if (task_text) {
        tasks.push({ task: task_text, completed: false });
        task_input.value = "";
        update_tasks_list();
    }
};

const update_tasks_list = () => {
    const taskList = document.querySelector(".task-list");
    taskList.innerHTML = "";

    tasks.forEach((task, index) => {
        const listItem = document.createElement("li");

        listItem.innerHTML = `
        <div class="taskItem">
            <div class="task ${task.completed ? "completed" : ""}">
                <input type="checkbox" class="task_checkbox" ${task.completed ? "checked" : ""} />
                <p>${task.task}</p>
            </div>

            <div class="bin_icon">
                <img src="dustbin.svg" onClick="deleteTask(${index})" />
            </div>
        </div>
        `;

        listItem.querySelector(".task_checkbox").addEventListener("change", () => toggle_Task_complete(index));

        taskList.appendChild(listItem);
  
    });

    update_stats();
};

const toggle_Task_complete = (index) => {
    tasks[index].completed = !tasks[index].completed; 
    update_tasks_list();
};

const deleteTask = (index) => {
    tasks.splice(index, 1);
    update_tasks_list();
};


document.getElementById("add_task").addEventListener("click", function (e) {
    e.preventDefault(); 
    new_task();
});


const update_stats = () => {
    const totalTasks = tasks.length;
    const completedTasks = tasks.filter(task => task.completed).length;

    document.getElementById("numbers").textContent = `${completedTasks} / ${totalTasks}`;

    const progressLine = document.getElementById("prog-line");
    const progressPercentage = (completedTasks / (totalTasks || 1) * 100);
    progressLine.style.width = `${progressPercentage}%`;
};

